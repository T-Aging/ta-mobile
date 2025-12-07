package com.example.tam.common.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "order_detail")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class OrderDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_detail_id")
    private Integer id;

    // OrderHeader와의 관계 설정 (지연 로딩)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private OrderHeader orderHeader;

    // Menu와의 관계 설정
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id", nullable = false)
    private Menu menu;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "temperature", length = 10)
    private String temperature;

    @Column(name = "size", length = 10)
    private String size;

    @Column(name = "order_detail_price", precision = 10, scale = 2)
    private BigDecimal orderDetailPrice;

    @OneToMany(mappedBy = "orderDetail", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OrderOption> orderOptions = new ArrayList<>();
    
    // 연관관계 편의 메소드
    public void addOrderOption(OrderOption orderOption) {
        this.orderOptions.add(orderOption);
        orderOption.setOrderDetail(this);
    }
}