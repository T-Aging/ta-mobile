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

    @Transactional
    public AuthDto.LoginResponse loginWithKakaoCode(String code) {
        log.info("인가 코드로 로그인 시도 (Mock): {}", code);
        return loginWithKakao("mock_access_token_" + code); 
    }

    public AuthDto.LoginResponse refreshToken(AuthDto.TokenRefreshRequest request) {
        log.info("토큰 갱신 요청: {}", request.getRefreshToken());
        String newToken = jwtUtil.generateToken(1L, "테스트유저");
        return AuthDto.LoginResponse.builder()
                .accessToken(newToken)
                .refreshToken(request.getRefreshToken())
                .userId(1L)
                .build();
    }

    @Transactional
    public AuthDto.LoginResponse loginWithKakao(String accessToken) {
        String kakaoId = "KAKAO_" + System.currentTimeMillis(); 
        String fetchedPhoneNumber = "010-1234-5678"; 
        
        Kakao kakao = kakaoRepository.findById(kakaoId)
                .orElseGet(() -> {
                    User newUser = User.builder()
                            .username("사용자" + kakaoId.substring(6, 10))
                            .phoneNumber(fetchedPhoneNumber)
                            .build();
                    User savedUser = userRepository.save(newUser);

                    // [수정] userId(Integer) -> user(User) 객체 주입
                    Kakao newKakao = Kakao.builder()
                            .kakaoId(kakaoId)
                            .user(savedUser) 
                            .accessToken(accessToken)
                            .lastAccessDate(LocalDateTime.now())
                            .build();
                    return kakaoRepository.save(newKakao);
                });

        // [수정] kakao.getUserId() -> kakao.getUser().getUserId()
        // Kakao 엔티티는 User 객체를 참조하고 있으므로 getter를 통해 접근해야 함
        User user = kakao.getUser(); 
        if (user == null) {
             throw new RuntimeException("사용자를 찾을 수 없습니다");
        }
        
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

    public void logout(Integer userId) {
        log.info("로그아웃 - userId: {}", userId);
    }
}