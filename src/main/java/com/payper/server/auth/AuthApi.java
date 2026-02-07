package com.payper.server.auth;

import com.payper.server.auth.dto.request.LoginRequest;
import com.payper.server.auth.dto.response.LoginSuccessResponse;
import com.payper.server.auth.dto.response.ReissueSuccessResponse;
import com.payper.server.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;

@Tag(name = "인증", description = "로그인, 토큰 재발급, 로그아웃 API")
public interface AuthApi {

    @Operation(summary = "로그인", description = "OAuth 토큰으로 로그인 (미가입 시 자동 회원가입). Access Token은 응답 바디, Refresh Token은 HttpOnly 쿠키로 발급됩니다.", security = {})
    ResponseEntity<ApiResponse<LoginSuccessResponse>> enroll(
            LoginRequest loginRequest,
            HttpServletResponse response
    );

    @Operation(summary = "토큰 재발급", description = "Refresh Token(쿠키)으로 새로운 Access Token을 발급합니다.", security = {})
    ResponseEntity<ApiResponse<ReissueSuccessResponse>> reissue(
            @Parameter(description = "Refresh Token (HttpOnly 쿠키로 자동 전송)") String refreshToken,
            HttpServletResponse response
    );

    @Operation(summary = "로그아웃", description = "Refresh Token을 무효화하고 쿠키를 삭제합니다.", security = {})
    ResponseEntity<ApiResponse<String>> logout(
            @Parameter(description = "Refresh Token (HttpOnly 쿠키로 자동 전송)") String refreshToken,
            HttpServletResponse response
    );
}
