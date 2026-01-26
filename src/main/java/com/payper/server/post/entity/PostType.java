package com.payper.server.post.entity;

import lombok.Getter;

@Getter
public enum PostType {

    /**
     * 혜택
     */
    BENEFIT,

    /**
     * 질문
     */
    QUESTION,

    /**
     * 기타
     */
    ETC
}