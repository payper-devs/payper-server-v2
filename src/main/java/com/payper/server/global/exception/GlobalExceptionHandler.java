package com.payper.server.global.exception;

import com.payper.server.auth.AuthException;
import com.payper.server.global.response.ApiResponse;
import com.payper.server.global.response.ErrorCode;
import com.payper.server.global.response.FieldErrorDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 비즈니스 예외
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiResponse<Void>> handleApiException(ApiException e) {
        log.warn("[API_EXCEPTION] code={}, message={}", e.getErrorCode().getCode(), e.getErrorCode().getMessage(), e);
        return buildErrorResponse(e.getErrorCode());
    }

    // 잘못된 요청
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgumentException(IllegalArgumentException e) {
        log.warn("[ILLEGAL_ARGUMENT] message={}", e.getMessage(), e);
        return buildErrorResponse(ErrorCode.BAD_REQUEST);
    }

    // @Valid 전용
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<List<FieldErrorDto>>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        List<FieldErrorDto> fieldErrors = e.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> new FieldErrorDto
                        (fieldError.getField(), fieldError.getDefaultMessage())).toList();

        log.warn("[VALIDATION_FAILED] errors={}", fieldErrors);

        ErrorCode errorCode = ErrorCode.BAD_REQUEST;
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ApiResponse.fail(errorCode, fieldErrors));
    }

    // 시큐리티 필터 밖에서 발생한 Auth 예외
    @ExceptionHandler(AuthException.class)
    public ResponseEntity<ApiResponse<Void>> handleAuthException(AuthException e) {
        log.warn("[AUTH_EXCEPTION] code={}, message={}", e.getErrorCode().getCode(), e.getErrorCode().getMessage(), e);
        return buildErrorResponse(e.getErrorCode());
    }

    // 그 외 모든 예외 - 서버 오류
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
        log.error("[UNEXPECTED_EXCEPTION]", e);
        return buildErrorResponse(ErrorCode.INTERNAL_SERVER_ERROR);
    }

    // ======================
    // 공통 응답 생성 헬퍼
    // ======================
    private ResponseEntity<ApiResponse<Void>> buildErrorResponse(ErrorCode errorCode) {
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ApiResponse.fail(errorCode));
    }
}
