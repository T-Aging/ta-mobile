package com.example.tam.common.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "order_detail")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class OrderDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_detail_id")
    private Integer orderDetailId; // id -> orderDetailId 변경

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private OrderHeader orderHeader;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id", nullable = false)
    private Menu menu;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "temperature", length = 10)
    private String temperature;

    @Column(name = "size", length = 10)
    private String size;

    @Column(name = "order_detail_price")
    private Integer orderDetailPrice; // BigDecimal -> Integer

    @OneToMany(mappedBy = "orderDetail", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OrderOption> orderOptions = new ArrayList<>();
    
    public void addOrderOption(OrderOption orderOption) {
        this.orderOptions.add(orderOption);
        orderOption.setOrderDetail(this);
    }
}