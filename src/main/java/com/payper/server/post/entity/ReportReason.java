package com.payper.server.post.entity;

import lombok.Getter;

@Getter
public enum ReportReason {
    SPAM_OR_ADVERTISEMENT("스팸 또는 광고성 콘텐츠"),
    OBSCENE_CONTENT("음란하거나 선정적인 콘텐츠"),
    ILLEGAL_CONTENT("불법적인 정보를 포함한 콘텐츠"),
    FALSE_INFORMATION("허위 또는 오해를 유발하는 정보"),
    HARMFUL_CONTENT_TO_YOUTH("청소년에게 유해한 콘텐츠"),
    ABUSIVE_LANGUAGE("욕설, 비하, 모욕적 표현"),
    HATE_OR_DISCRIMINATION("혐오 또는 차별적 표현"),
    PERSONAL_INFORMATION_EXPOSURE("개인정보 노출"),
    OFFENSIVE_CONTENT("불쾌감을 유발하는 콘텐츠"),
    ETC("기타");

    private final String description;

    ReportReason(String description) {
        this.description = description;
    }
}
