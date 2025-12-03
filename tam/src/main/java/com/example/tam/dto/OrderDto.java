package com.example.tam.dto;

import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class OrderDto {
    
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
        private String fromDate; // "yyyy-MM-dd"
        private String toDate;   // "yyyy-MM-dd"
    }

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
        private Integer waitingNum;
        private Integer totalPrice;
        private String orderStatus;
        private List<OrderItemDetail> items;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrderItemDetail {
        private Integer menuId;
        private String menuName;
        private Integer quantity;
        private Integer price;
        private List<OptionDetail> options;
    }
}
