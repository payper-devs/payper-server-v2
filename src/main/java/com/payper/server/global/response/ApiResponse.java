package com.payper.server.global.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@Builder
@Schema(description = "공통 API 응답")
public class ApiResponse<T> {
    @Schema(description = "HTTP 상태 코드", example = "200")
    private final Integer status;
    @Schema(description = "응답 데이터")
    private final T data;
    @Schema(description = "에러 정보 (성공 시 null)")
    private final ExceptionDto error;

    public static <T> ApiResponse<T> ok() {
        return ApiResponse
                .<T>builder()
                .status(HttpStatus.OK.value())
                .data(null)
                .error(null)
                .build();
    }

    public static <T> ApiResponse<T> ok(@Nullable T data) {
        return ApiResponse
                .<T>builder()
                .status(HttpStatus.OK.value())
                .data(data)
                .error(null)
                .build();
    }

    public static <T> ApiResponse<T> created(@Nullable T data) {
        return ApiResponse
                .<T>builder()
                .status(HttpStatus.CREATED.value())
                .data(data)
                .error(null)
                .build();
    }

    public static <T> ApiResponse<T> fail(ErrorCode errorCode) {
        return ApiResponse
                .<T>builder()
                .status(errorCode.getStatus().value())
                .data(null)
                .error(ExceptionDto.of(errorCode))
                .build();
    }

    public static <T> ApiResponse<T> fail(ErrorCode errorCode, T data) {
        return ApiResponse
                .<T>builder()
                .status(errorCode.getStatus().value())
                .data(data)
                .error(ExceptionDto.of(errorCode))
                .build();
    }
}
