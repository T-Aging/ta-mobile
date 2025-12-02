package com.example.tam.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalDateTime;

public class OrderDto {
    
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateRequest {
        @NotBlank(message = "주문 항목은 필수입니다")
        private String itemsJson;
        
        @NotNull(message = "총 금액은 필수입니다")
        private Integer totalPrice;
        
        private String kioskId;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateFromCustomRequest {
        @NotNull(message = "커스텀 메뉴 ID는 필수입니다")
        private Long customMenuId;
        
        @NotNull(message = "수량은 필수입니다")
        private Integer quantity;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DateRangeRequest {
        @NotBlank(message = "시작일은 필수입니다")
        private String fromDate;
        
        @NotBlank(message = "종료일은 필수입니다")
        private String toDate;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private Long id;
        private String orderNumber;
        private String itemsJson;
        private Integer totalPrice;
        private String status;
        private String kioskId;
        private String receiptUrl;
        private LocalDateTime createdAt;
        private LocalDateTime completedAt;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SummaryResponse {
        private Long id;
        private String orderNumber;
        private Integer totalPrice;
        private String status;
        private LocalDateTime createdAt;
    }
}
