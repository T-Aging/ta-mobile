package com.example.tam.modules.user;
import org.springframework.beans.factory.annotation.Value; 
import org.springframework.web.servlet.view.RedirectView;
import com.example.tam.dto.ApiResponse;
import com.example.tam.dto.AuthDto;
import com.example.tam.modules.auth.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/t-age")
@RequiredArgsConstructor
@Tag(name = "인증", description = "회원가입 및 로그인 API")
public class AuthController {
    
    private final AuthService authService;

    // application.properties에서 값 가져오기
    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String kakaoClientId;
    
    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String kakaoRedirectUri;

    // [수정] 진짜 카카오 로그인 페이지로 이동시키는 메서드
    @GetMapping("/signup")
    public RedirectView kakaoSignupRedirect() {
        String reqUrl = "https://kauth.kakao.com/oauth/authorize" +
                        "?client_id=" + kakaoClientId +
                        "&redirect_uri=" + kakaoRedirectUri +
                        "&response_type=code";
        return new RedirectView(reqUrl);
    }

    // 카카오 인증 코드 콜백 처리
    @GetMapping("/login/oauth2/code/kakao")
    public ResponseEntity<ApiResponse<AuthDto.LoginResponse>> kakaoCallback(@RequestParam String code) {
        log.info("카카오 코드 수신: {}", code);
        AuthDto.LoginResponse response = authService.loginWithKakaoCode(code);
        return ResponseEntity.ok(ApiResponse.success("로그인 성공", response));
    }

    // 액세스 토큰으로 로그인
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthDto.LoginResponse>> loginWithKakao(
            @Valid @RequestBody AuthDto.KakaoLoginRequest request) {
        AuthDto.LoginResponse response = authService.loginWithKakao(request.getAccessToken());
        return ResponseEntity.ok(ApiResponse.success("로그인 성공", response));
    }
    
    // 토큰 갱신
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthDto.LoginResponse>> refreshToken(
            @Valid @RequestBody AuthDto.TokenRefreshRequest request) {
        AuthDto.LoginResponse response = authService.refreshToken(request);
        return ResponseEntity.ok(ApiResponse.success("토큰 갱신 성공", response));
    }
}