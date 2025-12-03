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
    public AuthDto.LoginResponse loginWithKakao(String accessToken) {
        // TODO: 실제 카카오 API 호출하여 사용자 정보 가져오기
        String kakaoId = "KAKAO_" + System.currentTimeMillis();
        
        Kakao kakao = kakaoRepository.findById(kakaoId)
                .orElseGet(() -> {
                    User newUser = User.builder()
                            .username("사용자" + kakaoId.substring(6, 10))
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

        kakao.setAccessToken(accessToken);
        kakao.setLastAccessDate(LocalDateTime.now());
        kakaoRepository.save(kakao);

        User user = userRepository.findById(kakao.getUserId())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));

        log.info("카카오 로그인 성공 - userId: {}", user.getUserId());

        return AuthDto.LoginResponse.builder()
                .userId(Long.valueOf(user.getUserId()))
                .username(user.getUsername())
                .email(null)
                .accessToken(accessToken)
                .refreshToken(null)
                .tokenType("Bearer")
                .expiresIn(3600L)
                .build();
    }

    public void logout(Integer userId) {
        log.info("로그아웃 - userId: {}", userId);
    }
}
