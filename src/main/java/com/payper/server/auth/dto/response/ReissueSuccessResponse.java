package com.payper.server.auth.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Schema(description = "토큰 재발급 성공 응답")
public class ReissueSuccessResponse {
    @Schema(description = "새로 발급된 JWT Access Token", example = "eyJhbGciOiJIUzI1NiJ9...")
    private String accessToken;
}
