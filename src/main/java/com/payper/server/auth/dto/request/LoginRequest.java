package com.payper.server.auth.dto.request;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "로그인 요청")
public class LoginRequest {
    @Schema(description = "OAuth Provider가 제공한 access token", example = "kakao_oauth_token_example")
    @NotBlank(message="OAuth Provider가 제공한 oauth resource access token이 필요합니다.")
    private String oauthToken;
}
