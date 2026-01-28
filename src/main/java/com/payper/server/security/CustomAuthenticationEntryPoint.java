package com.payper.server.security;

import com.payper.server.auth.exception.UserAuthenticationException;
import com.payper.server.auth.jwt.exception.JwtValidAuthenticationException;
import com.payper.server.global.response.ApiResponse;
import com.payper.server.global.response.ErrorCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@RequiredArgsConstructor
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException, ServletException {

        if (response.isCommitted()) {
            return;
        }

        ErrorCode errorCode = resolveErrorCode(request, authException);

        ApiResponse<Void> body = ApiResponse.fail(errorCode);

        response.setStatus(errorCode.getStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        objectMapper.writeValue(response.getWriter(), body);
    }

    private ErrorCode resolveErrorCode(HttpServletRequest request, AuthenticationException ex) {

        // 1) 내가 만든 커스텀 예외면 그대로 사용
        if (ex instanceof JwtValidAuthenticationException jwtEx) {
            return jwtEx.getErrorCode();
        }
        if(ex instanceof  UserAuthenticationException userEx) {
            return userEx.getErrorCode();
        }

        // 3) 토큰이 아예 없거나(익명 접근) 등으로 발생하는 대표 케이스
        if (ex instanceof InsufficientAuthenticationException) {
            return ErrorCode.UNAUTHENTICATED;
        }

        // 4) 최후의 기본값
        return ErrorCode.UNAUTHENTICATED;
    }

}