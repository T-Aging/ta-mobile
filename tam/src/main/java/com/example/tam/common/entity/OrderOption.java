package com.example.tam.common.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "order_option")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class OrderOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_option_id")
    private Integer id; // 얘는 Service에서 getOptionId를 찾는지 id를 찾는지 확인 필요하지만 일단 유지

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_detail_id", nullable = false)
    private OrderDetail orderDetail;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private OptionGroup optionGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "value_id", nullable = false)
    private OptionValue optionValue;

    @Builder.Default
    @Column(name = "extra_num", nullable = false)
    private Integer extraNum = 1;

    @Builder.Default
    @Column(name = "extra_price", precision = 10, scale = 2, nullable = false)
    private BigDecimal extraPrice = BigDecimal.ZERO;

    // ▼▼▼ 이 메서드가 없어서 에러가 났습니다. 꼭 추가해주세요! ▼▼▼
    public void setOrderDetail(OrderDetail orderDetail) {
        this.orderDetail = orderDetail;
    }
}