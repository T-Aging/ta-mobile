package com.example.tam.modules.auth;

import com.example.tam.common.entity.User;
import com.example.tam.common.entity.Kakao;
import com.example.tam.dto.AuthDto;
import com.example.tam.modules.user.UserRepository;
import com.example.tam.security.JwtUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UserRepository userRepository;
    private final KakaoRepository kakaoRepository;
    private final JwtUtil jwtUtil;

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String kakaoClientId;

    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String kakaoRedirectUri;
        @Transactional
        public AuthDto.LoginResponse loginOrRegisterByPhone(AuthDto.PhoneLoginRequest request) {
                
                // 1. 전화번호 하이픈 제거 (선택 사항: 010-1234-5678 -> 01012345678)
                String cleanPhone = request.getPhoneNumber().replaceAll("-", "").trim();

                // 2. DB 조회 (있으면 가져오고, 없으면 새로 생성)
                User user = userRepository.findByPhoneNumber(cleanPhone)
                        .orElseGet(() -> {
                        // 신규 회원가입 로직
                        log.info("새로운 전화번호 회원 가입: {}", cleanPhone);
                        User newUser = User.builder()
                                .username(request.getName()) // 요청받은 이름 사용
                                .phoneNumber(cleanPhone)
                                .build();
                        return userRepository.save(newUser);
                        });

                // 3. (옵션) 기존 회원인데 이름이 바뀌었을 경우 업데이트 하려면 여기에 로직 추가
                // if (!user.getUsername().equals(request.getName())) { ... }

                // 4. JWT 토큰 발급 (기존 로직 활용)
                // User Entity의 ID 타입에 따라 Long.valueOf 필요 여부 확인 (기존 코드 따름)
                String token = jwtUtil.generateToken(Long.valueOf(user.getUserId()), user.getUsername());
                String refreshToken = jwtUtil.generateRefreshToken(Long.valueOf(user.getUserId()));

                // 5. 응답 반환
                return AuthDto.LoginResponse.builder()
                        .userId(Long.valueOf(user.getUserId()))
                        .username(user.getUsername())
                        .accessToken(token)
                        .refreshToken(refreshToken)
                        .tokenType("Bearer")
                        .expiresIn(jwtUtil.getExpirationTime())
                        .build();
        }
    /**
     * [핵심] 인가 코드(qsh...)를 받아서 -> 액세스 토큰으로 교환 -> 로그인 진행
     */
    @Transactional
    public AuthDto.LoginResponse loginWithKakaoCode(String code) {
        // 1. 인가 코드로 카카오에서 "진짜 액세스 토큰" 받아오기
        String realAccessToken = getAccessTokenFromKakao(code);
        log.info("카카오 액세스 토큰 발급 성공: {}", realAccessToken);
        
        // 2. 받아온 토큰으로 로그인 로직 수행
        return loginWithKakao(realAccessToken);
    }
    
    /**
     * 카카오 액세스 토큰으로 로그인 (사용자 정보 조회 및 DB 저장)
     */
    @Transactional
    public AuthDto.LoginResponse loginWithKakao(String accessToken) {
        // 1. 카카오 API 호출하여 사용자 정보 가져오기
        JsonNode kakaoUserInfo = getKakaoUserInfo(accessToken);
        
        // 2. ID 및 정보 추출
        String kakaoId = kakaoUserInfo.get("id").asText();
        JsonNode properties = kakaoUserInfo.path("properties");
        JsonNode kakaoAccount = kakaoUserInfo.path("kakao_account");
        
        String nickname = properties.path("nickname").asText("Unknown User");
        // 이메일 등 추가 정보가 필요하면 여기서 kakaoAccount.path("email") 등으로 추출
        
        // 3. DB 조회 또는 회원가입
        Kakao kakao = kakaoRepository.findById(kakaoId)
                .orElseGet(() -> {
                    // 새 유저 생성
                    User newUser = User.builder()
                            .username(nickname)
                            .phoneNumber(null) 
                            .build();
                    User savedUser = userRepository.save(newUser);

                    // 카카오 연동 정보 저장
                    Kakao newKakao = Kakao.builder()
                            .kakaoId(kakaoId)
                            .userId(savedUser.getUserId())
                            .accessToken(accessToken)
                            .lastAccessDate(LocalDateTime.now())
                            .build();
                    return kakaoRepository.save(newKakao);
                });

        // 4. JWT 토큰 발급
        User user = userRepository.findById(kakao.getUserId())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));
        
        String token = jwtUtil.generateToken(Long.valueOf(user.getUserId()), user.getUsername());
        String refreshToken = jwtUtil.generateRefreshToken(Long.valueOf(user.getUserId()));

        return AuthDto.LoginResponse.builder()
                .userId(Long.valueOf(user.getUserId()))
                .username(user.getUsername())
                .accessToken(token)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtUtil.getExpirationTime())
                .build();
    }

    // [추가된 메서드] 인가 코드를 액세스 토큰으로 바꾸는 기능
    private String getAccessTokenFromKakao(String code) {
        RestTemplate rt = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", kakaoClientId);
        params.add("redirect_uri", kakaoRedirectUri); // http://localhost:8080/t-age/login/oauth2/code/kakao
        params.add("code", code); // 여기에 qsh... 코드가 들어갑니다

        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(params, headers);

        try {
            ResponseEntity<String> response = rt.exchange(
                    "https://kauth.kakao.com/oauth/token",
                    HttpMethod.POST,
                    kakaoTokenRequest,
                    String.class
            );

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            return jsonNode.get("access_token").asText();
        } catch (Exception e) {
            log.error("토큰 발급 실패: {}", e.getMessage());
            throw new RuntimeException("카카오 토큰 발급 실패. 인가 코드가 만료되었거나 리다이렉트 URI가 일치하지 않습니다.");
        }
    }

    // 사용자 정보 가져오기
   private JsonNode getKakaoUserInfo(String accessToken) {
        RestTemplate rt = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);

        HttpEntity<Void> entity = new HttpEntity<>(null, headers);

        try {
                ResponseEntity<String> response = rt.exchange(
                        "https://kapi.kakao.com/v2/user/me",
                        HttpMethod.GET,      // GET 권장
                        entity,
                        String.class
                );

                ObjectMapper objectMapper = new ObjectMapper();
                return objectMapper.readTree(response.getBody());
        } catch (Exception e) {
                throw new RuntimeException("카카오 사용자 정보 조회 실패: " + e.getMessage());
    }
}
    
    // 리프레시 토큰 (기존 유지)
    public AuthDto.LoginResponse refreshToken(AuthDto.TokenRefreshRequest request) {
        String newToken = jwtUtil.generateToken(1L, "테스트유저");
        return AuthDto.LoginResponse.builder()
                .accessToken(newToken)
                .refreshToken(request.getRefreshToken())
                .userId(1L)
                .build();
    }
    
    // QR 로그인 (기존 유지)
    @Transactional(readOnly = true)
    public AuthDto.LoginResponse loginByQrCode(String qrCode) {
        User user = userRepository.findByUserQr(qrCode)
                .orElseThrow(() -> new RuntimeException("유효하지 않은 QR 코드입니다."));

        String token = jwtUtil.generateToken(Long.valueOf(user.getUserId()), user.getUsername());
        String refreshToken = jwtUtil.generateRefreshToken(Long.valueOf(user.getUserId()));

        return AuthDto.LoginResponse.builder()
                .userId(Long.valueOf(user.getUserId()))
                .username(user.getUsername())
                .accessToken(token)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtUtil.getExpirationTime())
                .build();
    }
    public void unlinkKakao(String accessToken) {
        RestTemplate rt = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);

        HttpEntity<Void> entity = new HttpEntity<>(null, headers);

        try {
            ResponseEntity<String> response = rt.exchange(
                    "https://kapi.kakao.com/v1/user/unlink",
                    HttpMethod.POST,
                    entity,
                    String.class
            );
            log.info("카카오 연결 끊기 성공. 응답 ID: {}", response.getBody());
        } catch (Exception e) {
            // 토큰 만료 등으로 실패할 수 있으나, 서비스 탈퇴는 계속 진행되어야 하므로 로그만 남김
            log.error("카카오 연결 끊기 실패 (토큰 만료 가능성 있음): {}", e.getMessage());
        }
    }
    public void logout(Integer userId) {
        // 로그아웃 로직
    }
}