package com.example.tam.common.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "menu_option")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MenuOption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "option_id")
    private Integer optionId;

    @Column(name = "option_class")
    private String optionClass;

    @Column(name = "option_detail")
    private String optionDetail;

    @Column(name = "extra_price")
    private Integer extraPrice;
}
