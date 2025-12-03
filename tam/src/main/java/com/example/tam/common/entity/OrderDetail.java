package com.example.tam.common.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "order_detail")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class OrderDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_detail_id")
    private Integer orderDetailId;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "order_detail_price")
    private Integer orderDetailPrice;

    @Column(name = "order_id", nullable = false)
    private Integer orderId;

    @Column(name = "menu_id", nullable = false)
    private Integer menuId;
}
