package com.payper.server.auth.jwt.exception;

import com.payper.server.global.response.ErrorCode;
import lombok.Getter;
import org.springframework.security.core.AuthenticationException;

@Getter
public class JwtValidAuthenticationException extends AuthenticationException {
    private final ErrorCode errorCode;

    public JwtValidAuthenticationException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
