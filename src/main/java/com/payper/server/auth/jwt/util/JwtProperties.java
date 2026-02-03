package com.payper.server.auth.jwt.util;

import lombok.AccessLevel;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Getter
@Component
public class JwtProperties {
    @Value("${jwt.secret-key}")
    private String secretKey;

    @Value("${jwt.refresh-token.secret-key}")
    private String refreshTokenSecretKey;

    @Value("${jwt.access-token.exptime}")
    @Getter(AccessLevel.NONE)
    private Duration accessTokenExpiration;

    @Value("${jwt.refresh-token.exptime}")
    @Getter(AccessLevel.NONE)
    private Duration refreshTokenExpiration;

    public long getAccessTokenTime() {
        return accessTokenExpiration.toMillis();
    }

    public long getRefreshTokenTime() {
        return refreshTokenExpiration.toMillis();
    }
}