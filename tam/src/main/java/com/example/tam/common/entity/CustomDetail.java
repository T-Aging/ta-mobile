package com.example.tam.common.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "custom_detail")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CustomDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "custom_detail_id")
    private Integer customDetailId;

    @Column(name = "option_id", nullable = false)
    private Integer optionId;

    @Column(name = "custom_id", nullable = false)
    private Integer customId;

    @Column(name = "extra_num")
    private Integer extraNum;
}
