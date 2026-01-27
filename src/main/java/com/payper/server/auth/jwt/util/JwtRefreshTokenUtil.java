package com.payper.server.auth.jwt.util;

import com.payper.server.auth.jwt.RefreshTokenRepository;
import com.payper.server.auth.jwt.entity.RefreshTokenEntity;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Base64;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtRefreshTokenUtil {
    private final JwtProperties jwtProperties;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtParseUtil jwtParseUtil;

    private SecretKey refreshSecretKey;

    @PostConstruct
    protected void init() {
        refreshSecretKey = new SecretKeySpec(
                jwtProperties.getRefreshTokenSecretKey()
                        .getBytes(StandardCharsets.UTF_8),
                Jwts.SIG.HS512.key().build().getAlgorithm()
        );
    }

    public RefreshTokenEntity generateRefreshTokenEntity(
            String userIdentifier, String refreshToken
    ) {

        return RefreshTokenEntity.create(
                userIdentifier,
                hashRefreshToken(refreshToken)
        );
    }

    private String hashRefreshToken(String refreshToken) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(refreshSecretKey);
            byte[] digest = mac.doFinal(refreshToken.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(digest);
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Failed to hash refresh token", e);
        }
    }

    public void upsertRefreshTokenEntity(RefreshTokenEntity refreshTokenEntity) {
        deleteAllRefreshTokenEntity(refreshTokenEntity.getUserIdentifier());
        refreshTokenRepository.save(refreshTokenEntity);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public int deleteAllRefreshTokenEntity(String userIdentifier) {
        int count = refreshTokenRepository.deleteByUserIdentifier(userIdentifier);
        refreshTokenRepository.flush();
        return count;
    }

    public RefreshTokenEntity getRefreshTokenEntity(String refreshToken) {
        return refreshTokenRepository
                .findByHashedRefreshToken(hashRefreshToken(refreshToken))
                .orElse(null);
    }


    public void generateCookieRefreshToken(String refreshToken, HttpServletResponse response) {
        Cookie cookie = new Cookie("refreshToken", refreshToken);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        int age = (int) ((jwtParseUtil.getExpiresAt(refreshToken).getTime() - new Date().getTime()) / 1000);
        cookie.setMaxAge(age);
        response.addCookie(cookie);
    }

    public void eraseCookieRefreshToken(HttpServletResponse response) {
        Cookie cookie = new Cookie("refreshToken", null);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}
