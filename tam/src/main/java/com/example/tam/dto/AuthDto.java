package com.example.tam.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

public class AuthDto {
    
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class KakaoLoginRequest {
        @NotBlank(message = "카카오 액세스 토큰은 필수입니다")
        private String accessToken;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginResponse {
        private Long userId;
        private String username;
        private String email;
        private String accessToken;
        private String refreshToken;
        
        @Builder.Default // [수정] 이 어노테이션 추가
        private String tokenType = "Bearer";
        
        private Long expiresIn;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TokenRefreshRequest {
        @NotBlank
        private String refreshToken;
    }

    @Getter
    @NoArgsConstructor
    public static class PhoneLoginRequest {
        
        private String name; // 이름 (선택 혹은 필수)

        @NotBlank(message = "전화번호는 필수입니다.") // 빈 값이 오면 에러 발생
        private String phoneNumber;
    }
}