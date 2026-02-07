package com.payper.server.global.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "필드 유효성 검증 에러")
public class FieldErrorDto {
    @Schema(description = "에러 발생 필드명", example = "title")
    private final String field;
    @Schema(description = "에러 메시지", example = "제목을 적어주세요.")
    private final String message;
}
