package com.payper.server.global.exception;

import com.payper.server.global.response.ApiResponse;
import com.payper.server.global.response.ErrorCode;
import com.payper.server.global.response.FieldErrorDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 비즈니스 예외
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiResponse<Void>> handleApiException(ApiException e) {
        return buildErrorResponse(e.getErrorCode());
    }

    // 잘못된 요청
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgumentException(IllegalArgumentException e) {
        return buildErrorResponse(ErrorCode.BAD_REQUEST);
    }

    // @Valid 전용
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<List<FieldErrorDto>>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        List<FieldErrorDto> fieldErrors = e.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> new FieldErrorDto
                        (fieldError.getField(), fieldError.getDefaultMessage())).toList();

        ErrorCode errorCode = ErrorCode.BAD_REQUEST;
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ApiResponse.fail(errorCode, fieldErrors));
    }

    // 그 외 모든 예외 - 서버 오류
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
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
