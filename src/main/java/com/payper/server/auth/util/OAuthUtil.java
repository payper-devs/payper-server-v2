package com.payper.server.auth.util;

public interface OAuthUtil {
    OAuthUserInfo getUserInfoFromOAuthToken(String oAuthToken);
}
