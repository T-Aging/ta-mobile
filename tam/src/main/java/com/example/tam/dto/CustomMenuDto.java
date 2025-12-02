package com.example.tam.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalDateTime;

public class CustomMenuDto {
    
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateRequest {
        @NotBlank(message = "메뉴 이름은 필수입니다")
        private String name;
        
        private String description;
        
        @NotBlank(message = "옵션 정보는 필수입니다")
        private String optionsJson;
        
        @NotNull(message = "가격은 필수입니다")
        private Integer totalPrice;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateRequest {
        @NotNull
        private Long id;
        
        private String name;
        private String description;
        private String optionsJson;
        private Integer totalPrice;
        private Boolean isFavorite;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private Long id;
        private String name;
        private String description;
        private String optionsJson;
        private Integer totalPrice;
        private Boolean isFavorite;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }
}
