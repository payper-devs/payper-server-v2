package com.payper.server.auth.exception;

import com.payper.server.global.response.ErrorCode;
import lombok.Getter;
import org.springframework.security.core.AuthenticationException;

@Getter
public class UserAuthenticationException extends AuthenticationException {
    private final ErrorCode errorCode;

    public UserAuthenticationException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
