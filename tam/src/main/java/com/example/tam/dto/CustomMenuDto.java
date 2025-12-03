package com.example.tam.dto;

import lombok.*;
import java.util.List;

public class CustomMenuDto {
    
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CreateRequest {
        private Integer menuId;
        private String customName;
        private List<OptionDetail> options;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OptionDetail {
        private Integer optionId;
        private Integer extraNum; // 추가 개수
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UpdateRequest {
        private Integer customId;
        private String customName;
        private List<OptionDetail> options;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Integer customId;
        private Integer userId;
        private Integer menuId;
        private String menuName;
        private String customName;
        private Integer totalPrice;
        private List<OptionDetail> options;
    }
}
