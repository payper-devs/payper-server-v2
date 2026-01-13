package com.payper.server.telecom.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Plan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    // TODO: 테이블로 빼는 게 더 나을까? CardCompany랑 비교하기
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TelecomCompany company;
}