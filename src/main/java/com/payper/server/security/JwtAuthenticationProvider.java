package com.payper.server.security;

import com.payper.server.auth.AuthException;
import com.payper.server.auth.jwt.entity.JwtType;
import com.payper.server.auth.jwt.util.JwtParseUtil;
import com.payper.server.global.response.ErrorCode;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationProvider implements AuthenticationProvider {
    private final UserDetailsService userDetailsService;
    private final JwtParseUtil jwtParseUtil;

    @Override
    public @Nullable Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String accessToken = (String) authentication.getCredentials();

        if (jwtParseUtil.getJwtType(accessToken) != JwtType.ACCESS) {
            throw new AuthException(ErrorCode.JWT_ERROR);
        }

        String userIdentifier = jwtParseUtil.getUserIdentifier(accessToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(userIdentifier);

        return UsernamePasswordAuthenticationToken.authenticated(userDetails, null, userDetails.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}

