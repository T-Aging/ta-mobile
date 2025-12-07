package com.example.tam.common.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "order_header")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class OrderHeader {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Integer orderId; // id -> orderId 변경

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "session_id")
    private String sessionId;

    @Column(name = "order_datetime")
    private LocalDateTime orderDatetime; // Service에서 찾는 이름(getTimestamp 등 확인 필요하지만 일단 유지)

    @Column(name = "order_date")
    private LocalDate orderDate;

    @Column(name = "waiting_num")
    private Integer waitingNum;

    @Column(name = "total_price")
    private Integer totalPrice; // BigDecimal -> Integer (Service 로직에 맞춤)

    @Column(name = "order_state")
    private String orderStatus; // Enum 대신 String으로 Service가 사용중일 수 있어 수정 (또는 Service 수정)
    // *주의: Service코드가 getOrderStatus()를 String으로 받고 있어서 String으로 변경했습니다.

    @OneToMany(mappedBy = "orderHeader", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OrderDetail> orderDetails = new ArrayList<>();

    public void addDetail(OrderDetail detail){
        orderDetails.add(detail);
        detail.setOrderHeader(this);
    }
}