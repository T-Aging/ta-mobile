package com.example.tam.dto;

import lombok.*;
import java.time.LocalDateTime;

public class PushDto {
    
    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private Long id;
        private String type;
        private String title;
        private String message;
        private Boolean isRead;
        private LocalDateTime createdAt;
    }
}
