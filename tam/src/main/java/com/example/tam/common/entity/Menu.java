package com.example.tam.common.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "menu")
public class Menu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "menu_id")
    private Integer id;

    @Column(name = "menu_name", length = 100)
    private String name;

    @Column(name = "menu_image", length = 512)
    private String menuImage;

    @Column(name = "menu_price", precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "description", length = 100)
    private String description;

    @Column(name = "calorie")
    private Integer calorie;

    @Column(name="sugar")
    private Integer sugar;

    @Column(name = "caffeine")
    private Integer caffeine;

    @Enumerated(EnumType.STRING)
    @Column(name = "allergic")
    @Builder.Default
    private Allergen allergen = Allergen.NONE;

    public enum Allergen {
        NONE, MILK, SOY, NUTS, EGG
    }
}