package com.payper.server.auth;

import com.payper.server.auth.dto.JoinRequest;
import com.payper.server.auth.dto.LoginRequest;
import com.payper.server.auth.dto.LoginSuccessResponse;
import com.payper.server.auth.dto.ReissueSuccessResponse;
import com.payper.server.global.response.ApiResponse;
import com.payper.server.user.entity.AuthType;
import com.payper.server.user.entity.User;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/join")
    public ResponseEntity<ApiResponse<LoginSuccessResponse>> join(
            @RequestBody JoinRequest joinRequest,
            HttpServletResponse response
    ) {
        User user = authService.join(joinRequest);


        return issueTokensAndCreateResponse(user, response);
    }


    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginSuccessResponse>> login(
            @RequestBody LoginRequest loginRequest,
            HttpServletResponse response
    ) {
        User user = authService.findUserWithOauthToken(
                loginRequest.getOauthToken(),
                AuthType.KAKAO
        );


        return issueTokensAndCreateResponse(user, response);
    }

    private ResponseEntity<ApiResponse<LoginSuccessResponse>>
    issueTokensAndCreateResponse(User user, HttpServletResponse response) {
        String accessToken = authService.enrollNewAuthTokens(user, response);
        return ResponseEntity.ok(ApiResponse.ok(new LoginSuccessResponse(accessToken)));
    }

    @PostMapping("/reissue")
    public ResponseEntity<ApiResponse<ReissueSuccessResponse>> reissue(
            @CookieValue(required = false) String refreshToken,
            HttpServletResponse response
    ){
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
