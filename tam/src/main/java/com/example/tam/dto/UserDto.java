package com.example.tam.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import java.time.LocalDateTime;

public class UserDto {
    
    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserResponse {
        private Long id;
        private String username;
        private String email;
        private String phone;
        private String profileImageUrl;
        private LocalDateTime createdAt;
        private LocalDateTime lastLoginAt;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserUpdateRequest {
        private String username;
        
        @Email(message = "올바른 이메일 형식이 아닙니다")
        private String email;
        
        @Pattern(regexp = "^01[0-9]-?[0-9]{3,4}-?[0-9]{4}$", 
                 message = "올바른 전화번호 형식이 아닙니다")
        private String phone;
    }
}
