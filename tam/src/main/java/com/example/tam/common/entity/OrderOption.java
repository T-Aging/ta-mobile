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
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_detail_id", nullable = false)
    private OrderDetail orderDetail;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private OptionGroup optionGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "value_id", nullable = false)
    private OptionValue optionValue;

    // ▼ 경고 해결을 위해 @Builder.Default 추가
    @Builder.Default
    @Column(name = "extra_num", nullable = false)
    private Integer extraNum = 1;

    // ▼ 경고 해결을 위해 @Builder.Default 추가
    @Builder.Default
    @Column(name = "extra_price", precision = 10, scale = 2, nullable = false)
    private BigDecimal extraPrice = BigDecimal.ZERO;
}