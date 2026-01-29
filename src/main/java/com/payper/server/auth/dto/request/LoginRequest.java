package com.payper.server.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {

    //@NotBlank(message="OAuth Provider가 제공한 oauth resource access token이 필요합니다.")
    private String oauthToken;
}
