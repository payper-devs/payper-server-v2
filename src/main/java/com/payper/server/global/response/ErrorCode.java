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
    REISSUE_ERROR("JWT_005", HttpStatus.INTERNAL_SERVER_ERROR, "토큰 리이슈 중 예외"),

    //AUTHENTICATION - GENERAL
    UNAUTHENTICATED("SEC-001", HttpStatus.UNAUTHORIZED, "인증 필요"),
    UNAUTHORIZED("SEC-002", HttpStatus.FORBIDDEN, "접근권한 없음"),
    MEMBER_DUPLICATE("SEC-003", HttpStatus.INTERNAL_SERVER_ERROR, "이미 가입 유저 존재"),
    MEMBER_NOTFOUND("SEC-004", HttpStatus.INTERNAL_SERVER_ERROR, "유저 없음"),
    OAUTH_RESOURCE_ERROR("OAUTH-001", HttpStatus.SERVICE_UNAVAILABLE, "OAuth 리소스 서버와 통신 중 예외");


    // USER
    USER_NOT_FOUND("USER-001", HttpStatus.NOT_FOUND, "User Not Found"),
    NOT_POSTING_USER("USER-002", HttpStatus.FORBIDDEN, "Not Posting User"),

    // POST
    POST_NOT_FOUND("POST-001", HttpStatus.NOT_FOUND, "Post Not Found"),

    // MERCHANT
    MERCHANT_NOT_FOUND("MERCHANT-001", HttpStatus.NOT_FOUND, "Merchant Not Found")

    ;

    private final String code;
    private final HttpStatus status;
    private final String message;
}
