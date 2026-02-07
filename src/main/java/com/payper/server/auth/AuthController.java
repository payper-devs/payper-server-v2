package com.payper.server.auth;

import com.payper.server.auth.dto.request.LoginRequest;
import com.payper.server.auth.dto.response.LoginSuccessResponse;
import com.payper.server.auth.dto.response.ReissueSuccessResponse;
import com.payper.server.auth.util.OAuthUserInfo;
import com.payper.server.global.response.ApiResponse;
import com.payper.server.user.entity.AuthType;
import com.payper.server.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "인증", description = "로그인, 토큰 재발급, 로그아웃 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    @Operation(summary = "로그인", description = "OAuth 토큰으로 로그인 (미가입 시 자동 회원가입). Access Token은 응답 바디, Refresh Token은 HttpOnly 쿠키로 발급됩니다.", security = {})
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


    @Operation(summary = "토큰 재발급", description = "Refresh Token(쿠키)으로 새로운 Access Token을 발급합니다.", security = {})
    @PostMapping("/reissue")
    public ResponseEntity<ApiResponse<ReissueSuccessResponse>> reissue(
            @Parameter(description = "Refresh Token (HttpOnly 쿠키로 자동 전송)")
            @CookieValue(required = false) String refreshToken,
            HttpServletResponse response
    ) {
        String accessToken = authService.reissueAccessToken(refreshToken, response);

        return ResponseEntity.ok(
                ApiResponse.ok(new ReissueSuccessResponse(accessToken))
        );
    }

    @Operation(summary = "로그아웃", description = "Refresh Token을 무효화하고 쿠키를 삭제합니다.", security = {})
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(
            @Parameter(description = "Refresh Token (HttpOnly 쿠키로 자동 전송)")
            @CookieValue(required = false) String refreshToken,
            HttpServletResponse response
    ) {
        authService.clearRefreshTokenAndEntity(refreshToken, response);

        return ResponseEntity.ok(ApiResponse.ok("logout success"));
    }
}
