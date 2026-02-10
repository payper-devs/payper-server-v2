package com.payper.server.global.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    //GENERAL
    BAD_REQUEST("GEN-001", HttpStatus.BAD_REQUEST, "Bad Request"),

    NOT_FOUND("GEN-003", HttpStatus.NOT_FOUND, "Not Found"),
    CONFLICT("GEN-004", HttpStatus.CONFLICT, "Conflict"),
    INTERNAL_SERVER_ERROR("GEN-005", HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error"),

  
    // AUTHENTICATION - JWT
    JWT_ERROR("JWT_001", HttpStatus.UNAUTHORIZED, "토큰 만료 외 예외"),
    JWT_EXPIRED("JWT_002", HttpStatus.UNAUTHORIZED, "토큰 만료"),
    JWT_REISSUE_ERROR("JWT_003", HttpStatus.INTERNAL_SERVER_ERROR, "리프레시 토큰 만료 외 예외"),
    JWT_REISSUE_EXPIRED("JWT_004", HttpStatus.INTERNAL_SERVER_ERROR, "리프레시 토큰 만료"),
    JWT_REISSUE_OLD("JWT_005", HttpStatus.INTERNAL_SERVER_ERROR, "만료 리프레시 토큰 사용"),
    REISSUE_ERROR("JWT_006", HttpStatus.INTERNAL_SERVER_ERROR, "토큰 리이슈 중 예외"),

    //AUTHENTICATION - GENERAL
    UNAUTHENTICATED("SEC-001", HttpStatus.UNAUTHORIZED, "인증 필요"),
    UNAUTHORIZED("SEC-002", HttpStatus.FORBIDDEN, "접근권한 없음"),
    USER_DUPLICATE("SEC-003", HttpStatus.INTERNAL_SERVER_ERROR, "이미 가입 유저 존재"),
    USER_NOT_FOUND_AUTH("SEC-004", HttpStatus.INTERNAL_SERVER_ERROR, "인증 과정 중 유저 없음 발생"),
    USER_INACTIVE("SEC-005", HttpStatus.INTERNAL_SERVER_ERROR, "해당 유저 비활성"),

    OAUTH_RESOURCE_ERROR("OAUTH-001", HttpStatus.SERVICE_UNAVAILABLE, "OAuth 리소스 서버와 통신 중 예외"),


    // USER
    USER_NOT_FOUND("USER-001", HttpStatus.NOT_FOUND, "User Not Found"),
    NOT_AN_ADMIN("USER-002", HttpStatus.FORBIDDEN, "Not An Admin"),

    // POST
    POST_NOT_FOUND("POST-001", HttpStatus.NOT_FOUND, "Post Not Found"),
    NOT_POST_AUTHOR("POST-002", HttpStatus.FORBIDDEN, "Not Post Author"),

    // COMMENT
    COMMENT_NOT_FOUND("COMMENT-001", HttpStatus.NOT_FOUND, "Comment Not Found"),
    NOT_COMMENT_AUTHOR("COMMENT-002", HttpStatus.FORBIDDEN, "Not Comment Author"),
    POST_NOT_COMMENTABLE("COMMENT-003", HttpStatus.BAD_REQUEST, "Cannot comment on deleted or inactive post"),
    INVALID_PARENT_COMMENT("COMMENT-004", HttpStatus.BAD_REQUEST, "Invalid parent comment"),

    // MERCHANT
    MERCHANT_NOT_FOUND("MERCHANT-001", HttpStatus.NOT_FOUND, "Merchant Not Found"),
    MERCHANT_ALREADY_EXISTS("MERCHANT-002", HttpStatus.CONFLICT, "Merchant Already Exists"),

    // CATEGORY
    CATEGORY_NOT_FOUND("CATEGORY-001", HttpStatus.NOT_FOUND, "Category Not Found"),
    CATEGORY_ALREADY_EXISTS("CATEGORY-002", HttpStatus.CONFLICT, "Category Already Exists"),
    CATEGORY_DEPTH_EXCEEDED("CATEGORY-003", HttpStatus.BAD_REQUEST, "Category Depth Exceeded"),
    CATEGORY_CANNOT_BE_SELF_PARENT("CATEGORY-004", HttpStatus.BAD_REQUEST, "Category Cannot Be Self Parent"),
    PARENT_CATEGORY_CANNOT_HAVE_PARENT("CATEGORY-005", HttpStatus.BAD_REQUEST, "Parent Category Cannot Be Changed To A Child"),

    ;

    private final String code;
    private final HttpStatus status;
    private final String message;
}
