package com.payper.server.post.entity;

import lombok.Getter;

@Getter
public enum PostType {
    /**
     * 카드 혜택
     */
    BENEFIT_CARD,

    /**
     * 요금제 혜택
     */
    BENEFIT_PLAN,

    /**
     * 프로모션 -> 가맹점에서 단독으로 진행하는 행사
     */
    PROMOTION,

    /**
     * 질문
     */
    QUESTION,

    /**
     * 기타
     */
    ETC
}