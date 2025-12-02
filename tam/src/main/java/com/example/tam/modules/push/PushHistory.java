package com.example.tam.modules.push.entity;


import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import com.example.tam.modules.user.entity.User;


@Entity
@Table(name = "push_history")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class PushHistory {


@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;


@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "user_id")
private User user;


private String type; // kiosk-confirm / kakao-open-api / receipt-arrived
private String message;


private LocalDateTime sentAt;


@PrePersist
public void prePersist() {
sentAt = LocalDateTime.now();
}
}
