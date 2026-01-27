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
    JWT_ERROR("JWT_001", HttpStatus.UNAUTHORIZED, "JWT General Error"),
    JWT_EXPIRED("JWT_002", HttpStatus.UNAUTHORIZED, "JWT Expired"),
    JWT_REISSUE_ERROR("JWT_003", HttpStatus.INTERNAL_SERVER_ERROR, "Refresh Token General Error"),
    JWT_REISSUE_EXPIRED("JWT_004", HttpStatus.INTERNAL_SERVER_ERROR, "Refresh Token Expired"),
    REISSUE_ERROR("JWT_005", HttpStatus.INTERNAL_SERVER_ERROR, "Reissue General Error"),

    //AUTHENTICATION - GENERAL
    UNAUTHENTICATED("SEC-001", HttpStatus.UNAUTHORIZED, "Unauthenticated"),
    UNAUTHORIZED("SEC-002", HttpStatus.FORBIDDEN, "Unauthorized"),
    USER_DUPLICATE("SEC-003", HttpStatus.INTERNAL_SERVER_ERROR, "Duplicate User"),
    USER_NOTFOUND("SEC-004", HttpStatus.INTERNAL_SERVER_ERROR, "User Not Exists"),
    OAUTH_RESOURCE_ERROR("OAUTH-001", HttpStatus.SERVICE_UNAVAILABLE, "OAuth Resource Unavailable");


    private final String code;
    private final HttpStatus status;
    private final String message;
}
