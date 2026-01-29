package com.payper.server.auth.jwt.exception;

import com.payper.server.global.exception.ApiException;
import com.payper.server.global.response.ErrorCode;

public class ReissueException extends ApiException {
    public ReissueException(ErrorCode errorCode) {
        super(errorCode);
    }
}
