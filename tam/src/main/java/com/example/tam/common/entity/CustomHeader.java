package com.example.tam.common.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "custom_header")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CustomHeader {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "custom_id")
    private Integer customId;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "menu_id", nullable = false)
    private Integer menuId;

    @Column(name = "custom_name")
    private String customName;
}
