package com.payper.server.auth;

import com.payper.server.auth.exception.OAuthException;
import com.payper.server.auth.exception.UserAuthenticationException;
import com.payper.server.auth.jwt.exception.ReissueException;
import com.payper.server.global.response.ApiResponse;
import com.payper.server.global.response.ErrorCode;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(
        assignableTypes = {
                AuthController.class,
        }
)
@Order(Ordered.HIGHEST_PRECEDENCE)
public class AuthGlobalExceptionHandler {

    @ExceptionHandler(OAuthException.class)
    public ResponseEntity<ApiResponse<Void>> handleOAuthException(final OAuthException exception) {
        return buildErrorResponse(exception.getErrorCode());
    }

    @ExceptionHandler(UserAuthenticationException.class)
    public ResponseEntity<ApiResponse<Void>> handleUserException(final UserAuthenticationException exception) {
        return buildErrorResponse(exception.getErrorCode());
    }

    //AuthController 내에서 발생하는 JwtValidAuthentication 에러는 토큰 리이슈 관련 에러입니다.
    @ExceptionHandler(ReissueException.class)
    public ResponseEntity<ApiResponse<Void>> handleReissueException(
            final ReissueException exception
    ) {
        return buildErrorResponse(exception.getErrorCode());
    }


    private ResponseEntity<ApiResponse<Void>> buildErrorResponse(ErrorCode errorCode) {
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ApiResponse.fail(errorCode));
    }
}
