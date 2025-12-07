package com.example.tam.common.entity;

import jakarta.persistence.*;
import lombok.*;

// BigDecimal 제거 (Service가 int를 원함)
// import java.math.BigDecimal; 
import java.util.List;

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
    private Integer menuId; // id -> menuId 로 변경 (Service가 getMenuId()를 찾음)

    @Column(name = "menu_name", length = 100)
    private String menuName; // name -> menuName 로 변경 (Service가 getMenuName()을 찾음)

    @Column(name = "menu_image", length = 512)
    private String menuImage;

    @Column(name = "menu_price", precision = 10, scale = 2)
    private Integer menuPrice; // BigDecimal -> Integer 로 변경 (price -> menuPrice)

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