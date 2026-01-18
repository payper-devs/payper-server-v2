package com.payper.server.post.entity;

import lombok.Getter;

@Getter
public enum ReportReason { // TODO: 추천 받아요
    SPAM("스팸"),
    INSULT("욕설"),
    INAPPROPRIATE("부적절한 글"),
    AD("광고"),
    FALSE_INFORMATION("허위 정보"),
    HATE_SPEECH("혐오 표현"),
    OTHER("기타");

    private final String description;

    ReportReason(String description) {
        this.description = description;
    }
}
