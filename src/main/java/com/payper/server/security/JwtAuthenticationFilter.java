package com.payper.server.security;

import com.payper.server.auth.jwt.util.JwtParseUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final RequestMatcher skipRequestMatcher;
    private final JwtParseUtil jwtParseUtil;
    private final AuthenticationManager authenticationManager;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return skipRequestMatcher.matches(request);
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        //System.out.println("JwtAuthenticationFilter.doFilterInternal");

        String accessToken = jwtParseUtil.extractJwtTokenFromRequest(request);

        if (accessToken == null) {
            filterChain.doFilter(request, response);
            return;
        }

        try{
            Authentication requestAuth =
                    UsernamePasswordAuthenticationToken.unauthenticated(null, accessToken);

            Authentication authenticated =
                    authenticationManager.authenticate(requestAuth);
            SecurityContextHolder.getContext().setAuthentication(authenticated);
            filterChain.doFilter(request, response);
        }
        catch(AuthenticationException e){
            SecurityContextHolder.clearContext();
            customAuthenticationEntryPoint.commence(request, response, e);
        }
    }
}
