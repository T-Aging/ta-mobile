package com.example.tam.common.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "option_group")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class OptionGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_id")
    private Integer id;

    @Column(name = "group_name", nullable = false)
    private String name; // 예: "사이즈", "온도", "샷추가"

    // 필수 옵션 여부 등 추가 필드가 있을 수 있음
}