package com.payper.server.auth.exception;

import com.payper.server.global.exception.ApiException;
import com.payper.server.global.response.ErrorCode;

public class OAuthException extends ApiException {
    public OAuthException(ErrorCode errorCode) {
        super(errorCode);
    }
}
