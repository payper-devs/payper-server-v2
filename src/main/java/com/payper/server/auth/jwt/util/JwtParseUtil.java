package com.payper.server.auth.jwt.util;

import com.payper.server.auth.jwt.entity.JwtType;
import com.payper.server.auth.jwt.exception.JwtValidAuthenticationException;
import com.payper.server.global.response.ErrorCode;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtParseUtil {
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

    public String extractJwtTokenFromRequest(HttpServletRequest request) {
        String headerValue = request.getHeader("Authorization");

        if (StringUtils.hasText(headerValue) && headerValue.startsWith("Bearer ")) {
            String token = headerValue.substring(7);

            if (token.isEmpty())
                return null;

            return token;
        }

        return null;
    }

    public String getUserIdentifier(String jwtToken) {
        return getClaimsFromJwtToken(jwtToken)
                .getSubject();
    }

    public Date getIssuedAt(String jwtToken) {
        return getClaimsFromJwtToken(jwtToken)
                .getIssuedAt();
    }

    public Date getExpiresAt(String jwtToken) {
        return getClaimsFromJwtToken(jwtToken)
                .getExpiration();
    }

    public JwtType getJwtType(String jwtToken) {
        return JwtType.valueOf(getJws(jwtToken).getHeader().getType());
    }

    private Claims getClaimsFromJwtToken(String jwtToken) {
        return getJws(jwtToken).getPayload();
    }

    private Jws<Claims> getJws(String jwtToken) {
        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(jwtToken);
        } catch (ExpiredJwtException e) {
            throw new JwtValidAuthenticationException(ErrorCode.JWT_EXPIRED);
        } catch (UnsupportedJwtException | ClaimJwtException | SignatureException |
                 MalformedJwtException | IllegalArgumentException e) {
            throw new JwtValidAuthenticationException(ErrorCode.JWT_ERROR);
        }
    }
}
