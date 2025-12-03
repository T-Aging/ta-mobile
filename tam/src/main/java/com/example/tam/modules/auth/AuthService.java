package com.example.tam.modules.auth;

import com.example.tam.common.entity.User;
import com.example.tam.common.entity.Kakao;
import com.example.tam.dto.AuthDto;
import com.example.tam.modules.user.UserRepository;
import com.example.tam.security.JwtUtil; // JwtUtil 주입 필요
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
    private final JwtUtil jwtUtil; // 토큰 발급을 위해 추가

    @Transactional
    public AuthDto.LoginResponse loginWithKakao(String accessToken) {
        // 실제로는 카카오 API를 호출하여 사용자 정보를 받아와야 함 (여기서는 Mocking)
        String kakaoId = "KAKAO_" + System.currentTimeMillis(); 
        String fetchedPhoneNumber = "010-1234-5678"; // 카카오 API에서 받아온 전화번호라고 가정
        
        Kakao kakao = kakaoRepository.findById(kakaoId)
                .orElseGet(() -> {
                    // 신규 회원 가입
                    User newUser = User.builder()
                            .username("사용자" + kakaoId.substring(6, 10))
                            .phoneNumber(fetchedPhoneNumber) // 전화번호 저장
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

        // 기존 회원 업데이트
        User user = userRepository.findById(kakao.getUserId())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));
        
        // JWT 토큰 발급
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

    // 키오스크 QR 로그인 처리 메서드 추가
    @Transactional(readOnly = true)
    public AuthDto.LoginResponse loginByQrCode(String qrCode) {
        User user = userRepository.findByUserQr(qrCode) // User 엔티티/Repository에 해당 메서드 필요
                .orElseThrow(() -> new RuntimeException("유효하지 않은 QR 코드입니다."));

        String token = jwtUtil.generateToken(Long.valueOf(user.getUserId()), user.getUsername());
        
        return AuthDto.LoginResponse.builder()
                .userId(Long.valueOf(user.getUserId()))
                .username(user.getUsername())
                .accessToken(token)
                .tokenType("Bearer")
                .build();
    }

    public void logout(Integer userId) {
        log.info("로그아웃 - userId: {}", userId);
    }
}
