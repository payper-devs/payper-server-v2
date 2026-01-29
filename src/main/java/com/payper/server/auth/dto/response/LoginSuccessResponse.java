package com.payper.server.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LoginSuccessResponse {
    private String accessToken;
}
