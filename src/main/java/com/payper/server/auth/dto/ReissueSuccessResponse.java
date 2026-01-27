package com.payper.server.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ReissueSuccessResponse {
    private String accessToken;
}
