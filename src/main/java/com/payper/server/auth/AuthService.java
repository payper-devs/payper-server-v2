package com.payper.server.auth;

import com.payper.server.auth.dto.JoinRequest;
import com.payper.server.auth.exception.OAuthException;
import com.payper.server.auth.jwt.entity.JwtType;
import com.payper.server.auth.jwt.entity.RefreshTokenEntity;
import com.payper.server.auth.jwt.util.JwtParseUtil;
import com.payper.server.auth.jwt.util.JwtRefreshTokenUtil;
import com.payper.server.auth.jwt.util.JwtTokenUtil;
import com.payper.server.auth.util.KakaoOAuthUtilImpl;
import com.payper.server.auth.util.OAuthUserInfo;
import com.payper.server.global.response.ErrorCode;
import com.payper.server.user.UserService;
import com.payper.server.user.entity.AuthType;
import com.payper.server.user.entity.User;
import com.payper.server.user.entity.UserRole;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {
    private final UserService userService;
    private final KakaoOAuthUtilImpl kakaoOAuthUtil;
    private final JwtTokenUtil jwtTokenUtil;
    private final JwtRefreshTokenUtil jwtRefreshTokenUtil;
    private final JwtParseUtil jwtParseUtil;

    public User join(JoinRequest joinRequest) {
        String oauthToken = joinRequest.getOauthToken();
        OAuthUserInfo oauthUserInfo = kakaoOAuthUtil.getUserInfoFromOAuthToken(oauthToken);

        User user = User.create(
                AuthType.KAKAO,
                oauthUserInfo.getName(),
                oauthUserInfo.getOauthId(),
                UserRole.USER,
                true
        );

        return userService.save(user);
    }

    public String enrollNewAuthTokens(User user, HttpServletResponse response) {
        return upsertNewAuthTokens(user.getUserIdentifier(),response,new Date());
    }

    public User findUserWithOauthToken(String oauthToken, AuthType authType) {

        OAuthUserInfo oauthUserInfo = switch (authType) {
            case AuthType.KAKAO -> kakaoOAuthUtil.getUserInfoFromOAuthToken(oauthToken);
            default -> throw new OAuthException(ErrorCode.OAUTH_RESOURCE_ERROR);
        };

        return userService.getActiveOAuthUser(oauthUserInfo);
    }

    private String upsertNewAuthTokens(String userIdentifier, HttpServletResponse response, Date issuedAt) {
        String accessToken = jwtTokenUtil.generateJwtToken(JwtType.ACCESS, issuedAt, userIdentifier);
        String refreshToken = jwtTokenUtil.generateJwtToken(JwtType.REFRESH, issuedAt, userIdentifier);

        RefreshTokenEntity refreshTokenEntity =
                jwtRefreshTokenUtil.generateRefreshTokenEntity(userIdentifier, refreshToken);


        jwtRefreshTokenUtil.generateCookieRefreshToken(refreshToken, response);

        jwtRefreshTokenUtil.upsertRefreshTokenEntity(refreshTokenEntity);

        return accessToken;
    }


}
