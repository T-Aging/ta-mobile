package com.example.tam.common.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "order_option")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class OrderOption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_option_id")
    private Integer orderOptionId;

    @Column(name = "option_id", nullable = false)
    private Integer optionId;

    @Column(name = "order_detail_id", nullable = false)
    private Integer orderDetailId;

    @Column(name = "extra_num")
    private Integer extraNum;
}
