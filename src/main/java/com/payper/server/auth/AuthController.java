package com.payper.server.auth;

import com.payper.server.auth.dto.JoinRequest;
import com.payper.server.auth.dto.LoginRequest;
import com.payper.server.auth.dto.LoginSuccessResponse;
import com.payper.server.auth.dto.ReissueSuccessResponse;
import com.payper.server.auth.util.OAuthUserInfo;
import com.payper.server.global.response.ApiResponse;
import com.payper.server.user.UserService;
import com.payper.server.user.entity.AuthType;
import com.payper.server.user.entity.User;
import com.payper.server.user.entity.UserRole;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login") //로그인 시도 -> 필요하면 가입 -> 로그인
    public ResponseEntity<ApiResponse<LoginSuccessResponse>> enroll(
            @RequestBody LoginRequest loginRequest,
            HttpServletResponse response
    ) {
        //OAuth 리소스 서버와 통신 문제 생기면 예외
        OAuthUserInfo oauthUserInfo = authService.findOAuthUserInfo(
                loginRequest.getOauthToken(),
                AuthType.KAKAO
        );

        //유저가 inactive(밴, 정지) 되어있으면 예외
        User user = authService.findOrEnrollOAuthUser(oauthUserInfo);

        String accessToken = authService.enrollNewAuthTokens(user, response);

        return ResponseEntity.ok(ApiResponse.ok(new LoginSuccessResponse(accessToken)));
    }


    @PostMapping("/reissue")
    public ResponseEntity<ApiResponse<ReissueSuccessResponse>> reissue(
            @CookieValue(required = false) String refreshToken,
            HttpServletResponse response
    ) {
        String accessToken = authService.reissueAccessToken(refreshToken, response);

        return ResponseEntity.ok(
                ApiResponse.ok(new ReissueSuccessResponse(accessToken))
        );
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(
            @CookieValue(required = false) String refreshToken,
            HttpServletResponse response
    ) {
        authService.clearRefreshTokenAndEntity(refreshToken, response);

        return ResponseEntity.ok(ApiResponse.ok("logout success"));
    }
}
