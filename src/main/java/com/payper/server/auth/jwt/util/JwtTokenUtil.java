package com.payper.server.auth.jwt.util;

import com.payper.server.auth.jwt.entity.JwtType;
import io.jsonwebtoken.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtTokenUtil {
    private final JwtProperties jwtProperties;

    private SecretKey key;

    @PostConstruct
    protected void init() {
        key = new SecretKeySpec(
                jwtProperties.getSecretKey()
                        .getBytes(StandardCharsets.UTF_8),
                Jwts.SIG.HS512.key().build().getAlgorithm()
        );
    }

    public String generateJwtToken(JwtType jwtType, Date now, String userIdentifier) {
        Date expDate = new Date(
                now.getTime() +
                        (jwtType == JwtType.REFRESH ?
                                jwtProperties.getRefreshTokenTime() : jwtProperties.getAccessTokenTime())
        );

        return Jwts.builder()
                .header()
                .type(jwtType.name())
                .and()
                .subject(userIdentifier)
                .issuedAt(now)
                .expiration(expDate)
                .signWith(key)
                .id(UUID.randomUUID().toString())
                .compact();
    }

}
