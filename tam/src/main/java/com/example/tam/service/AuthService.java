package com.example.tam.service;

import com.example.tam.dto.AuthDto;
import com.example.tam.entity.User;
import com.example.tam.exception.KakaoAuthException;
import com.example.tam.exception.UnauthorizedException;
import com.example.tam.repository.UserRepository;
import com.example.tam.security.JwtUtil;
import com.example.tam.service.kakao.KakaoApiService;
import com.example.tam.service.kakao.KakaoOAuthService;
import com.example.tam.service.kakao.KakaoUserInfo;
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
    private final KakaoApiService kakaoApiService;
    private final KakaoOAuthService kakaoOAuthService;
    private final JwtUtil jwtUtil;

    @Transactional
    public AuthDto.LoginResponse loginWithKakaoCode(String code) {
        try {
            KakaoOAuthService.KakaoTokenResponse tokenResponse = 
                kakaoOAuthService.getAccessToken(code);
            
            KakaoUserInfo kakaoUserInfo = 
                kakaoApiService.getUserInfo(tokenResponse.getAccessToken());
            
            return processKakaoLogin(kakaoUserInfo);
            
        } catch (KakaoAuthException e) {
            log.error("카카오 인증 실패", e);
            throw e;
        } catch (Exception e) {
            log.error("로그인 처리 중 오류 발생", e);
            throw new RuntimeException("로그인 처리에 실패했습니다", e);
        }
    }

    @Transactional
    public AuthDto.LoginResponse loginWithKakao(AuthDto.KakaoLoginRequest request) {
        try {
            KakaoUserInfo kakaoUserInfo = 
                kakaoApiService.getUserInfo(request.getAccessToken());
            
            return processKakaoLogin(kakaoUserInfo);
            
        } catch (KakaoAuthException e) {
            log.error("카카오 인증 실패", e);
            throw e;
        } catch (Exception e) {
            log.error("로그인 처리 중 오류 발생", e);
            throw new RuntimeException("로그인 처리에 실패했습니다", e);
        }
    }

    private AuthDto.LoginResponse processKakaoLogin(KakaoUserInfo kakaoUserInfo) {
        if (kakaoUserInfo == null || kakaoUserInfo.getId() == null) {
            throw new KakaoAuthException("카카오 사용자 정보를 가져올 수 없습니다");
        }
        
        String kakaoId = String.valueOf(kakaoUserInfo.getId());
        
        User user = userRepository.findByKakaoId(kakaoId)
                .orElseGet(() -> createNewUser(kakaoUserInfo));
        
        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);
        
        String accessToken = jwtUtil.generateToken(user.getId(), user.getUsername());
        String refreshToken = jwtUtil.generateRefreshToken(user.getId());
        
        log.info("사용자 로그인 성공 - userId: {}, kakaoId: {}", user.getId(), kakaoId);
        
        return AuthDto.LoginResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtUtil.getExpirationTime())
                .build();
    }

    private User createNewUser(KakaoUserInfo kakaoUserInfo) {
        String kakaoId = String.valueOf(kakaoUserInfo.getId());
        String nickname = kakaoUserInfo.getProperties() != null 
                ? kakaoUserInfo.getProperties().getNickname() 
                : "사용자" + kakaoId.substring(0, 4);
        String email = kakaoUserInfo.getKakaoAccount() != null 
                ? kakaoUserInfo.getKakaoAccount().getEmail() 
                : null;
        String profileImage = kakaoUserInfo.getProperties() != null 
                ? kakaoUserInfo.getProperties().getProfileImage() 
                : null;
        
        User newUser = User.builder()
                .kakaoId(kakaoId)
                .username(nickname)
                .email(email)
                .profileImageUrl(profileImage)
                .role(User.UserRole.USER)
                .status(User.UserStatus.ACTIVE)
                .build();
        
        User savedUser = userRepository.save(newUser);
        log.info("신규 회원 가입 완료 - userId: {}, kakaoId: {}", savedUser.getId(), kakaoId);
        
        return savedUser;
    }

    @Transactional(readOnly = true)
    public AuthDto.LoginResponse refreshToken(AuthDto.TokenRefreshRequest request) {
        try {
            if (!jwtUtil.validateToken(request.getRefreshToken())) {
                throw new UnauthorizedException("유효하지 않은 리프레시 토큰입니다");
            }
            
            Long userId = jwtUtil.getUserIdFromToken(request.getRefreshToken());
            
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new UnauthorizedException("사용자를 찾을 수 없습니다"));
            
            String newAccessToken = jwtUtil.generateToken(user.getId(), user.getUsername());
            String newRefreshToken = jwtUtil.generateRefreshToken(user.getId());
            
            return AuthDto.LoginResponse.builder()
                    .userId(user.getId())
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .accessToken(newAccessToken)
                    .refreshToken(newRefreshToken)
                    .tokenType("Bearer")
                    .expiresIn(jwtUtil.getExpirationTime())
                    .build();
                    
        } catch (Exception e) {
            log.error("토큰 재발급 실패", e);
            throw new UnauthorizedException("토큰 재발급에 실패했습니다");
        }
    }

    public void logout(Long userId) {
        log.info("사용자 로그아웃 - userId: {}", userId);
    }
}
