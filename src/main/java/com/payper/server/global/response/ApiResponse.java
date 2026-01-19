package com.payper.server.global.response;

import jakarta.annotation.Nullable;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@Builder
public class ApiResponse<T> {
    private final HttpStatus status;
    private final T data;
    private final ExceptionDto error;

    public static <T> ApiResponse<T> ok(@Nullable T data) {
        return ApiResponse
                .<T>builder()
                .status(HttpStatus.OK)
                .data(data)
                .error(null)
                .build();
    }

    public static <T> ApiResponse<T> created(@Nullable T data) {
        return ApiResponse
                .<T>builder()
                .status(HttpStatus.CREATED)
                .data(data)
                .error(null)
                .build();
    }

    public static <T> ApiResponse<T> fail(ErrorCode errorCode) {
        return ApiResponse
                .<T>builder()
                .status(errorCode.getStatus())
                .data(null)
                .error(ExceptionDto.of(errorCode))
                .build();
    }
}
