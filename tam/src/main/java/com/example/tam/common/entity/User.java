package com.example.tam.common.entity;


import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false)
    private Integer userId;   // 회원 식별자 UUID (INT)

    @Column(name = "username")
    private String username;

    @Column(name = "profile_image")
    private String profileImage;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "signup_date")
    private LocalDateTime signupDate;

    @Column(name = "update_date")
    private LocalDateTime updateDate;

    @Column(name = "user_qr")
    private String userQr;
}
