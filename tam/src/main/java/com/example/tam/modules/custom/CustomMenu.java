package com.example.tam.modules.custom.entity;


import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import com.example.tam.modules.user.entity.User;


@Entity
@Table(name = "custom_menu")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class CustomMenu {


@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;


@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "user_id")
private User user;


private String name;
private String options; // JSON 형식 {"ice":true, "shot":1} 같은 형태
private boolean isRecent;


private LocalDateTime createdAt;
private LocalDateTime updatedAt;


@PrePersist
public void prePersist() {
createdAt = LocalDateTime.now();
updatedAt = LocalDateTime.now();
}


@PreUpdate
public void preUpdate() {
updatedAt = LocalDateTime.now();
}
}
