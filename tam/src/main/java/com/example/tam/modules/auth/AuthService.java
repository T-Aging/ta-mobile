package com.example.tam.modules.auth;

import com.example.tam.common.entity.User;
import com.example.tam.common.entity.Kakao;
import com.example.tam.dto.AuthDto;
import com.example.tam.modules.user.UserRepository;
import com.example.tam.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UserRepository userRepository;
    private final KakaoRepository kakaoRepository;
    private final JwtUtil jwtUtil;

    // [추가] 인가 코드로 로그인 (Mock)
    @Transactional
    public AuthDto.LoginResponse loginWithKakaoCode(String code) {
        log.info("인가 코드로 로그인 시도 (Mock): {}", code);
        // 실제로는 카카오 API로 토큰을 받아야 하지만, 테스트용으로 바로 로그인 처리
        return loginWithKakao("mock_access_token_" + code); 
    }

    // [추가] 리프레시 토큰 처리 (Mock)
    public AuthDto.LoginResponse refreshToken(AuthDto.TokenRefreshRequest request) {
        log.info("토큰 갱신 요청: {}", request.getRefreshToken());
        // 테스트용: ID 1번 유저로 간주하고 새 토큰 발급
        String newToken = jwtUtil.generateToken(1L, "테스트유저");
        return AuthDto.LoginResponse.builder()
                .accessToken(newToken)
                .refreshToken(request.getRefreshToken())
                .userId(1L)
                .build();
    }

    @Transactional
    public AuthDto.LoginResponse loginWithKakao(String accessToken) {
        // 기존 코드 유지
        String kakaoId = "KAKAO_" + System.currentTimeMillis(); 
        String fetchedPhoneNumber = "010-1234-5678"; 
        
        Kakao kakao = kakaoRepository.findById(kakaoId)
                .orElseGet(() -> {
                    User newUser = User.builder()
                            .username("사용자" + kakaoId.substring(6, 10))
                            .phoneNumber(fetchedPhoneNumber)
                            .build();
                    User savedUser = userRepository.save(newUser);

                    Kakao newKakao = Kakao.builder()
                            .kakaoId(kakaoId)
                            .userId(savedUser.getUserId())
                            .accessToken(accessToken)
                            .lastAccessDate(LocalDateTime.now())
                            .build();
                    return kakaoRepository.save(newKakao);
                });

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

    @Transactional(readOnly = true)
    public AuthDto.LoginResponse loginByQrCode(String qrCode) {
        // 기존 코드 유지 (findByUserQr 메소드가 UserRepository에 있어야 함)
        // 임시로 1번 유저 리턴
        String token = jwtUtil.generateToken(1L, "QR유저");
        return AuthDto.LoginResponse.builder().accessToken(token).userId(1L).build();
    }

    public void logout(Integer userId) {
        log.info("로그아웃 - userId: {}", userId);
    }
}