package com.payper.server.security;

import com.payper.server.auth.AuthException;
import com.payper.server.global.response.ApiResponse;
import com.payper.server.global.response.ErrorCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
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

        ErrorCode errorCode = resolveErrorCode(authException);

        ApiResponse<Void> body = ApiResponse.fail(errorCode);
        log.warn("[AUTH_EXCEPTION IN FILTER] code={}, message={}",body.getError().getCode(),body.getError().getMessage());

        response.setStatus(errorCode.getStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        objectMapper.writeValue(response.getWriter(), body);
    }

    private ErrorCode resolveErrorCode(AuthenticationException ex) {
        // 1) security filter 체인 속 auth 예외 발생 케이스
        if(ex instanceof AuthException authException) {
            return authException.getErrorCode();
        }

        // 2) 토큰이 아예 없거나(익명 접근) 등으로 발생하는 대표 케이스
        if (ex instanceof InsufficientAuthenticationException) {
            return ErrorCode.UNAUTHENTICATED;
        }

        // 3) 최후의 기본값
        return ErrorCode.UNAUTHENTICATED;
    }

}