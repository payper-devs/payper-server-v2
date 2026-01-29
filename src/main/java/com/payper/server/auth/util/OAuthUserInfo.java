package com.payper.server.auth.util;

import com.payper.server.user.entity.AuthType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OAuthUserInfo {
    private String name;

    private String oauthId;

    private AuthType authType;
}
