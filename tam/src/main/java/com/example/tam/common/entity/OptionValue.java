package com.example.tam.common.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "option_value")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class OptionValue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "value_id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private OptionGroup optionGroup; // 어느 그룹에 속하는지

    @Column(name = "value_name", nullable = false)
    private String name; // 예: "Large", "Ice"

    @Column(name = "extra_price")
    private BigDecimal extraPrice; // 이 값 자체의 추가금 (필요 시)
}