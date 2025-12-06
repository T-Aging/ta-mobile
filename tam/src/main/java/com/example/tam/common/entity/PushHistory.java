package com.example.tam.common.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "push_history")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PushHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "push_id")
    private Integer pushId;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "push_type")
    private String pushType;

    @Column(name = "message")
    private String message;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    @Column(name = "is_read")
    @Builder.Default // [수정] 이 어노테이션 추가
    private Boolean isRead = false;

    @PrePersist
    public void prePersist() {
        if (sentAt == null) sentAt = LocalDateTime.now();
        if (isRead == null) isRead = false;
    }
}