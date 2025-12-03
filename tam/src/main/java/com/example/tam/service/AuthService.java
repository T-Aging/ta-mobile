package com.example.tam.modules.auth;

import com.example.tam.common.entity.User;
import com.example.tam.common.entity.Kakao;
import com.example.tam.dto.AuthDto;
import com.example.tam.modules.user.UserRepository;
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

    @Transactional
    public AuthDto.LoginResponse loginWithKakao(String kakaoId, String accessToken) {
        // Kakao 정보 조회 또는 생성
        Kakao kakao = kakaoRepository.findById(kakaoId)
                .orElseGet(() -> {
                    // 신규 사용자 생성
                    User newUser = User.builder()
                            .username("사용자" + kakaoId.substring(0, 4))
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

        // 기존 사용자면 토큰 업데이트
        kakao.setAccessToken(accessToken);
        kakao.setLastAccessDate(LocalDateTime.now());
        kakaoRepository.save(kakao);

        User user = userRepository.findById(kakao.getUserId())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));

        log.info("카카오 로그인 성공 - userId: {}, kakaoId: {}", user.getUserId(), kakaoId);

        return AuthDto.LoginResponse.builder()
                .userId(Long.valueOf(user.getUserId()))
                .username(user.getUsername())
                .email(null) // User 엔티티에 email 필드 없음
                .accessToken(accessToken)
                .refreshToken(null) // 필요시 구현
                .tokenType("Bearer")
                .expiresIn(3600L)
                .build();
    }

    @Transactional
    public void logout(Integer userId) {
        log.info("로그아웃 - userId: {}", userId);
        // 필요시 토큰 무효화 로직 추가
    }
}
