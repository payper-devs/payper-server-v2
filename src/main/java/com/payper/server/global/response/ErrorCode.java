package com.payper.server.global.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    //GENERAL
    BAD_REQUEST("GEN-001", HttpStatus.BAD_REQUEST, "Bad Request"),
    UNAUTHORIZED("GEN-002", HttpStatus.UNAUTHORIZED, "Unauthorized"),
    NOT_FOUND("GEN-003", HttpStatus.NOT_FOUND, "Not Found"),
    CONFLICT("GEN-004", HttpStatus.CONFLICT, "Conflict"),
    INTERNAL_SERVER_ERROR("GEN-005", HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error"),

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
