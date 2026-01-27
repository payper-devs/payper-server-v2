package com.payper.server.auth.exception;

import com.payper.server.global.response.ErrorCode;
import lombok.Getter;
import org.springframework.security.core.AuthenticationException;

@Getter
public class MemberAuthenticationException extends AuthenticationException {
    private final ErrorCode errorCode;

    public MemberAuthenticationException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
