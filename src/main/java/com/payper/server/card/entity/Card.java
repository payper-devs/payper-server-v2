package com.payper.server.card.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    // TODO: enum으로 하는 게 더 나을까? TelecomCompany랑 비교하기
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private CardCompany company;
}