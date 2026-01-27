package com.payper.server.auth.util;

import com.payper.server.user.entity.AuthType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;

@Getter
@AllArgsConstructor
public class OAuthUserInfo {
    private String name;

    private String oauthId;

    private AuthType authType;
}
