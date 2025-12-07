package com.example.tam.common.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "order_option")
@Getter
@Setter
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

    // Service 코드가 optionId만 저장하려고 하므로 호환성을 위해 추가
    @Column(name = "option_id")
    private Integer optionId;

    // Group/Value는 나중에 사용할 수 있도록 Nullable로 변경
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = true)
    private OptionGroup optionGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "value_id", nullable = true)
    private OptionValue optionValue;

    @Builder.Default
    @Column(name = "extra_num", nullable = false)
    private Integer extraNum = 1;

    // Service가 Integer 계산을 하므로 Integer로 변경
    @Builder.Default
    @Column(name = "extra_price", nullable = false)
    private Integer extraPrice = 0;

    // 에러 났던 메서드 추가
    public void setOrderDetail(OrderDetail orderDetail) {
        this.orderDetail = orderDetail;
    }
}