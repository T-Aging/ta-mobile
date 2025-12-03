package com.example.tam.common.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "menu")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Menu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "menu_id")
    private Integer menuId;

    @Column(name = "menu_name")
    private String menuName;

    @Column(name = "menu_price")
    private Integer menuPrice;

    @Column(name = "description")
    private String description;

    @Column(name = "calorie")
    private Integer calorie;

    @Column(name = "sugar")
    private Integer sugar;

    @Column(name = "caffeine")
    private Integer caffeine;

    @Column(name = "allergic")
    private String allergic;
}
