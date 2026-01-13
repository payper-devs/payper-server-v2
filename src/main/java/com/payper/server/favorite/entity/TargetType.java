package com.payper.server.favorite.entity;

import lombok.Getter;

@Getter
public enum TargetType {
    /**
     * 가맹점
     */
    MERCHANT,

    /**
     * 카드
     */
    CARD,

    /**
     * 요금제
     */
    PLAN
}