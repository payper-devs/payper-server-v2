package com.payper.server.global.exception;

import com.payper.server.global.response.ApiResponse;
import com.payper.server.global.response.ErrorCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    //비즈니스 예외
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiResponse<Void>> handleApiException(ApiException e) {
        ErrorCode errorCode = e.getErrorCode();
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ApiResponse.fail(errorCode));
    }

    //잘못된 요청
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgumentException(IllegalArgumentException e) {
        ErrorCode errorCode = ErrorCode.BAD_REQUEST;
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ApiResponse.fail(errorCode));
    }

    //@Valid 전용
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        ErrorCode errorCode = ErrorCode.BAD_REQUEST;

        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ApiResponse.fail(errorCode));
    }

    //그 외 모든 예외 - 서버 오류
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
        ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ApiResponse.fail(errorCode));
    }
}
