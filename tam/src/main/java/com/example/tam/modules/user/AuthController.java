package com.example.tam.controller;

import com.example.tam.dto.ApiResponse;
import com.example.tam.dto.AuthDto;
import com.example.tam.service.AuthService;
import com.example.tam.service.kakao.KakaoOAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

@Slf4j
@RestController
@RequestMapping("/t-age")
@RequiredArgsConstructor
@Tag(name = "인증", description = "회원가입 및 로그인 API")
public class AuthController {
    
    private final AuthService authService;
    private final KakaoOAuthService kakaoOAuthService;

    @Operation(summary = "카카오 회원가입/로그인 페이지로 리다이렉트")
    @GetMapping("/signup")
    public RedirectView kakaoSignupRedirect() {
        String authUrl = kakaoOAuthService.getAuthorizationUrl();
        log.info("카카오 인증 페이지로 리다이렉트: {}", authUrl);
        return new RedirectView(authUrl);
    }

    @Operation(summary = "카카오 OAuth 콜백 처리")
    @GetMapping("/login/oauth2/code/kakao")
    public ResponseEntity<ApiResponse<AuthDto.LoginResponse>> kakaoCallback(
            @RequestParam String code) {
        log.info("카카오 OAuth 콜백 수신 - code: {}", code.substring(0, 10) + "...");
        
        AuthDto.LoginResponse response = authService.loginWithKakaoCode(code);
        return ResponseEntity.ok(ApiResponse.success("카카오 로그인 성공", response));
    }

    @Operation(summary = "카카오 액세스 토큰으로 로그인")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthDto.LoginResponse>> loginWithKakao(
            @Valid @RequestBody AuthDto.KakaoLoginRequest request) {
        log.info("카카오 토큰 로그인 요청");
        
        AuthDto.LoginResponse response = authService.loginWithKakao(request);
        return ResponseEntity.ok(ApiResponse.success("로그인 성공", response));
    }

    @Operation(summary = "토큰 갱신")
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthDto.LoginResponse>> refreshToken(
            @Valid @RequestBody AuthDto.TokenRefreshRequest request) {
        log.info("토큰 갱신 요청");
        
        AuthDto.LoginResponse response = authService.refreshToken(request);
        return ResponseEntity.ok(ApiResponse.success("토큰 갱신 성공", response));
    }
}
