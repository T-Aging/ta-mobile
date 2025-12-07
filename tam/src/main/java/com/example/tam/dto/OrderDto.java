package com.example.tam.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

public class OrderDto {
    
    // ... (CreateRequest 등 기존 클래스는 그대로 유지) ...
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CreateRequest {
        private Integer storeId;
        private List<OrderItem> items;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrderItem {
        private Integer menuId;
        private Integer quantity;
        private List<OptionDetail> options;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OptionDetail {
        private Integer optionId;
        private Integer extraNum;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DateRangeRequest {
        private String fromDate; 
        private String toDate;   
    }

    // [수정] 응답 객체에 화면 표시에 필요한 필드 추가
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Integer orderId;
        private Integer userId;
        private Integer storeId;
        private String storeName;
        private LocalDateTime orderDatetime;
        
        // [추가] 목록 화면용 필드
        private String representativeMenuName; // 예: "카페라떼 외 2건"
        private Integer totalQuantity;         // 예: 3 (총 개수)
        
        private Integer waitingNum;
        private Integer totalPrice;
        private String orderStatus;
        
        private List<OrderItemDetail> items;
    }

    // [수정] 상세 항목 및 옵션 정보 보강
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrderItemDetail {
        private Integer menuId;
        private String menuName;
        private Integer quantity;
        private Integer price; // (메뉴기본가 + 옵션가) * 수량
        private List<OptionItemDetail> options; // 이름 변경 OptionDetail -> OptionItemDetail
    }

    // [추가] 옵션 상세 정보 (카테고리 포함)
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OptionItemDetail {
        private Integer optionId;
        private String optionClass;  // 예: "사이즈", "온도", "추가"
        private String optionName;   // 예: "톨(355ml)", "아이스", "샷 추가"
        private Integer extraPrice;  // 예: 500
        private Integer quantity;    // 옵션 개수
    }
}