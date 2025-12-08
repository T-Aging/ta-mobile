package com.example.tam.modules.user;

import com.example.tam.dto.ApiResponse;
import com.example.tam.dto.AuthDto;
import com.example.tam.modules.auth.AuthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String kakaoClientId;
    
    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String kakaoRedirectUri;

    // 1. 사용자가 이 주소로 들어오면 카카오 로그인 페이지로 이동
    @GetMapping("/signup")
    public RedirectView kakaoSignupRedirect() {
        String reqUrl = "https://kauth.kakao.com/oauth/authorize" +
                        "?client_id=" + kakaoClientId +
                        "&redirect_uri=" + kakaoRedirectUri +
                        "&response_type=code";
        return new RedirectView(reqUrl);
    }

    // 2. 카카오 로그인이 완료되면 이 주소로 돌아옴 (인가 코드 code가 쿼리파라미터로 들어옴)
    @GetMapping("/login/oauth2/code/kakao")
    public ResponseEntity<ApiResponse<AuthDto.LoginResponse>> kakaoCallback(@RequestParam String code) {
        log.info("카카오 인가 코드 수신: {}", code);
        // 이제 qsh... 코드를 넣으면 내부적으로 토큰으로 바꿔서 로그인 시켜줌
        AuthDto.LoginResponse response = authService.loginWithKakaoCode(code);
        return ResponseEntity.ok(ApiResponse.success("로그인 성공", response));
    }
    
    // (선택) 앱에서 액세스 토큰을 직접 보내는 경우 (이건 기존 유지)
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthDto.LoginResponse>> loginWithKakao(
            @Valid @RequestBody AuthDto.KakaoLoginRequest request) {
        AuthDto.LoginResponse response = authService.loginWithKakao(request.getAccessToken());
        return ResponseEntity.ok(ApiResponse.success("로그인 성공", response));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthDto.LoginResponse>> refreshToken(
            @Valid @RequestBody AuthDto.TokenRefreshRequest request) {
        AuthDto.LoginResponse response = authService.refreshToken(request);
        return ResponseEntity.ok(ApiResponse.success("토큰 갱신 성공", response));
    }
}