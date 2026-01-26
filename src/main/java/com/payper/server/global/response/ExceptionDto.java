package com.payper.server.global.response;

import lombok.Getter;

@Getter
public class ExceptionDto {
    private final String code;
    private final String message;

    private ExceptionDto(ErrorCode errorCode) {
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
    }

    public static ExceptionDto of(ErrorCode errorCode) {
        return new ExceptionDto(errorCode);
    }
}
