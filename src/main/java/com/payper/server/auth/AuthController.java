package com.payper.server.auth;

import com.payper.server.auth.dto.JoinRequest;
import com.payper.server.auth.dto.LoginRequest;
import com.payper.server.auth.dto.LoginSuccessResponse;
import com.payper.server.global.response.ApiResponse;
import com.payper.server.user.entity.AuthType;
import com.payper.server.user.entity.User;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    private ResponseEntity<ApiResponse<LoginSuccessResponse>> issueTokensAndCreateResponse(User user, HttpServletResponse response) {
        String accessToken = authService.enrollNewAuthTokens(user, response);
        return ResponseEntity.ok(ApiResponse.ok(new LoginSuccessResponse(accessToken)));
    }
}
