package com.example.tam.modules.auth;

import com.example.tam.dto.ApiResponse;
import com.example.tam.dto.AuthDto;
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

    @Operation(summary = "카카오 회원가입")
    @GetMapping("/signup")
    public ResponseEntity<ApiResponse<String>> kakaoSignup(@RequestParam String code) {
        // 실제로는 카카오 인증 페이지로 리다이렉트
        return ResponseEntity.ok(ApiResponse.success("카카오 회원가입 페이지 리다이렉트", null));
    }

    @Operation(summary = "카카오 소셜 로그인")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthDto.LoginResponse>> loginWithKakao(
            @Valid @RequestBody AuthDto.KakaoLoginRequest request) {
        log.info("카카오 로그인 요청");
        AuthDto.LoginResponse response = authService.loginWithKakao(request.getAccessToken());
        return ResponseEntity.ok(ApiResponse.success("로그인 성공", response));
    }
}
