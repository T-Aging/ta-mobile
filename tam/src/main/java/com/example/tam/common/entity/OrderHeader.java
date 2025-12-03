package com.example.tam.common.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "order_header")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class OrderHeader {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Integer orderId;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "store_id", nullable = false)
    private Integer storeId;

    @Column(name = "order_datetime")
    private LocalDateTime orderDatetime;

    @Column(name = "order_date")
    private LocalDate orderDate;

    @Column(name = "waiting_num")
    private Integer waitingNum;

    @Column(name = "total_price")
    private Integer totalPrice;

    @Column(name = "order_status")
    private String orderStatus;
}
