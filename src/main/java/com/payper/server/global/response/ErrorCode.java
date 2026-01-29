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

    // POST
    POST_NOT_FOUND("POST-001", HttpStatus.NOT_FOUND, "Post Not Found"),
    NOT_POST_AUTHOR("POST-002", HttpStatus.FORBIDDEN, "Not Post Author"),

    // COMMENT
    COMMENT_NOT_FOUND("COMMENT-001", HttpStatus.NOT_FOUND, "Comment Not Found"),
    NOT_COMMENT_AUTHOR("COMMENT-002", HttpStatus.FORBIDDEN, "Not Comment Author"),
    POST_NOT_COMMENTABLE("COMMENT-003", HttpStatus.BAD_REQUEST, "Cannot comment on deleted or inactive post"),
    INVALID_PARENT_COMMENT("COMMENT-004", HttpStatus.BAD_REQUEST, "Invalid parent comment"),

    // MERCHANT
    MERCHANT_NOT_FOUND("MERCHANT-001", HttpStatus.NOT_FOUND, "Merchant Not Found")

    ;

    private final String code;
    private final HttpStatus status;
    private final String message;
}
