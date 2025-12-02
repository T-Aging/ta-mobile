package com.example.tam.modules.order.entity;


import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import com.example.tam.modules.user.entity.User;


@Entity
@Table(name = "orders")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Order {


@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;


@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "user_id")
private User user;


private String orderNumber; // 영수증 번호
private Integer totalPrice;


private LocalDateTime createdAt;


@PrePersist
public void prePersist() {
createdAt = LocalDateTime.now();
}
}
