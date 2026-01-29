package com.payper.server.post.dto;

import org.springframework.data.domain.Sort;

public enum PostSortType {

    POSTING_DATE("createdAt"),
    COMMENT_COUNT("commentCount"),
    LIKE_COUNT("likeCount"),
    VIEW_COUNT("viewCount");

    private final String property;

    PostSortType(String property) {
        this.property = property;
    }

    public Sort toSort(Sort.Direction direction) {
        return Sort.by(direction, property);
    }
}