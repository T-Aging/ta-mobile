package com.example.tam.common.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "kakao")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Kakao {

    @Id
    @Column(name = "kakao_id", nullable = false)
    private String kakaoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;   // FK: user.user_id

    @Column(name = "access_token")
    private String accessToken;

    @Column(name = "refresh_token")
    private String refreshToken;

    @Column(name = "last_access_date")
    private LocalDateTime lastAccessDate;
}

