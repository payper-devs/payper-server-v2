package com.payper.server.auth;

import com.payper.server.auth.jwt.entity.JwtType;
import com.payper.server.auth.jwt.entity.RefreshTokenEntity;
import com.payper.server.auth.jwt.util.JwtParseUtil;
import com.payper.server.auth.jwt.util.JwtRefreshTokenUtil;
import com.payper.server.auth.jwt.util.JwtTokenUtil;
import com.payper.server.auth.util.KakaoOAuthUtilImpl;
import com.payper.server.auth.util.OAuthUserInfo;
import com.payper.server.global.exception.ApiException;
import com.payper.server.global.response.ErrorCode;
import com.payper.server.user.UserService;
import com.payper.server.user.entity.AuthType;
import com.payper.server.user.entity.User;
import com.payper.server.user.entity.UserRole;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final UserService userService;
    private final KakaoOAuthUtilImpl kakaoOAuthUtil;

    private final JwtTokenUtil jwtTokenUtil;
    private final JwtRefreshTokenUtil jwtRefreshTokenUtil;
    private final JwtParseUtil jwtParseUtil;

    public User findOrEnrollOAuthUser(OAuthUserInfo oauthUserInfo) {
        //먼저 검증
        Optional<User> user = userService.getActiveOAuthUser(oauthUserInfo);

        user.ifPresent(
                u->{
                    log.info("가입 유저 확인 - userId: {}, userName: {}, userRole: {}", u.getId(),u.getName(),u.getUserRole().name());
                }
        );

        return user.orElseGet(
                () -> {
                    User savedUser = userService.save(
                            User.create(
                                    AuthType.KAKAO,
                                    oauthUserInfo.getName(),
                                    oauthUserInfo.getOauthId(),
                                    UserRole.USER,
                                    true
                            )
                    );

                    log.info("유저 가입 & 저장 - userId: {}, userName: {}, userRole: {}",
                            savedUser.getId(),savedUser.getName(),savedUser.getUserRole().name());

                    return savedUser;
                }
        );
    }

    public OAuthUserInfo findOAuthUserInfo(String oauthToken, AuthType authType) {
        return switch (authType) {
            case AuthType.KAKAO -> kakaoOAuthUtil.getUserInfoFromOAuthToken(oauthToken);
            default -> throw new ApiException(ErrorCode.OAUTH_RESOURCE_ERROR);
        };
    }

    public String enrollNewAuthTokens(User user, HttpServletResponse response) {
        Date issuedAt = new Date();

        upsertRefreshTokenAndEntity(user.getUserIdentifier(), response, issuedAt);
        String accessToken = upsertAccessToken(user.getUserIdentifier(), issuedAt);

        log.info("업서트 토큰 - userId: {}, issuedAt: {}",user.getId(),issuedAt);

        return accessToken;
    }

    public String reissueAccessToken(String refreshToken, HttpServletResponse response) {
        /* 1. 있는데, 만료되지 않음 -> 정상처리
         * 2. 있는데, 만료됨 -> 정상 리프레시 만료
         * 3. 없는데, 만료되지 않음 -> 리플레이 어택
         * 4. 없는데, 만료됨 -> 리플레이 어택
         * 5. 그냥 토큰이 이상함
         * */

        if (!StringUtils.hasText(refreshToken)) {
            throw new ApiException(ErrorCode.JWT_REISSUE_ERROR);
        }

        // 1) JWT 자체 검증(서명/만료/형식) + 타입 검사
        final String userIdentifier;
        final JwtType jwtType;
        try {
            jwtType = jwtParseUtil.getJwtType(refreshToken);
            if (jwtType != JwtType.REFRESH) {
                throw new ApiException(ErrorCode.JWT_REISSUE_ERROR);
            }
            userIdentifier = jwtParseUtil.getUserIdentifier(refreshToken);
        } catch (AuthException e) {
            throw
                    switch (e.getErrorCode()) {
                        case JWT_ERROR -> new ApiException(ErrorCode.JWT_REISSUE_ERROR);
                        case JWT_EXPIRED -> new ApiException(ErrorCode.JWT_REISSUE_EXPIRED);
                        default -> new ApiException(ErrorCode.REISSUE_ERROR);
                    };
        }

        // 2) DB에 없으면 리플레이 공격 의심 -> 해당 유저 토큰 전부 폐기
        Optional<RefreshTokenEntity> refreshTokenEntity = jwtRefreshTokenUtil.getRefreshTokenEntity(refreshToken);
        refreshTokenEntity.ifPresentOrElse(
                (r) -> {
                },
                () -> {
                    jwtRefreshTokenUtil.deleteAllRefreshTokenEntity(userIdentifier);
                    throw new ApiException(ErrorCode.JWT_REISSUE_OLD);
                }
        );

        Date reissuedAt = jwtParseUtil.getIssuedAt(refreshToken);
        upsertRefreshTokenAndEntity(userIdentifier, response, reissuedAt);
        String accessToken = upsertAccessToken(userIdentifier, new Date());

        log.info("토큰 재발급 완료 reissuedAt: {}",reissuedAt);

        return accessToken;
    }

    private String upsertAccessToken(String userIdentifier, Date issuedAt) {
        return jwtTokenUtil.generateJwtToken(JwtType.ACCESS, issuedAt, userIdentifier);
    }

    private void upsertRefreshTokenAndEntity(String userIdentifier, HttpServletResponse response, Date issuedAt) {
        String refreshToken = jwtTokenUtil.generateJwtToken(JwtType.REFRESH, issuedAt, userIdentifier);

        RefreshTokenEntity refreshTokenEntity =
                jwtRefreshTokenUtil.generateRefreshTokenEntity(userIdentifier, refreshToken);


        if(response!=null)
            jwtRefreshTokenUtil.generateCookieRefreshToken(refreshToken, response);

        jwtRefreshTokenUtil.upsertRefreshTokenEntity(refreshTokenEntity);
    }

    public void clearRefreshTokenAndEntity(String refreshToken, HttpServletResponse response) {
        jwtRefreshTokenUtil.eraseCookieRefreshToken(response);

        if (!StringUtils.hasText(refreshToken)) {
            return;
        }

        Optional<RefreshTokenEntity> refreshTokenEntity = jwtRefreshTokenUtil.getRefreshTokenEntity(refreshToken);
        refreshTokenEntity.ifPresent(
                r ->
                        jwtRefreshTokenUtil.deleteAllRefreshTokenEntity(r.getUserIdentifier())
        );

        log.info("리프레시 토큰 만료 완료");
    }
}
