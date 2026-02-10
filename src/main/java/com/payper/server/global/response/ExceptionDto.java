package com.payper.server.global.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "에러 정보")
public class ExceptionDto {
    @Schema(description = "에러 코드", example = "NOT_FOUND")
    private final String code;
    @Schema(description = "에러 메시지", example = "해당 리소스를 찾을 수 없습니다.")
    private final String message;

    private ExceptionDto(ErrorCode errorCode) {
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
    }

    public static ExceptionDto of(ErrorCode errorCode) {
        return new ExceptionDto(errorCode);
    }
}
