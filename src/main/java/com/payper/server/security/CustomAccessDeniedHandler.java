package com.payper.server.security;

import com.payper.server.global.response.ApiResponse;
import com.payper.server.global.response.ErrorCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;


import java.io.IOException;

@RequiredArgsConstructor
@Component
@Slf4j
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    private final ObjectMapper objectMapper;

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException
    ) throws IOException, ServletException {

        ApiResponse<String> failResponseDto;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.isAuthenticated())) {
            failResponseDto =
                    ApiResponse.fail(ErrorCode.UNAUTHENTICATED, accessDeniedException.getMessage());
        } else {
            failResponseDto =
                    ApiResponse.fail(ErrorCode.UNAUTHORIZED, accessDeniedException.getMessage());
        }

        log.warn("[AUTH_EXCEPTION IN FILTER] code={}, message={}",failResponseDto.getError().getCode(),failResponseDto.getError().getMessage());

        response.setStatus(failResponseDto.getStatus());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        objectMapper.writeValue(response.getWriter(), failResponseDto);
    }
}
