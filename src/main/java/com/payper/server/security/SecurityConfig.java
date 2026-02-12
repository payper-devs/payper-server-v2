package com.payper.server.security;

import com.payper.server.auth.jwt.util.JwtParseUtil;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import tools.jackson.databind.ObjectMapper;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private RequestMatcher permitAllRequestMatcher;
    private RequestMatcher authenticatedRequestMatcher;
    private RequestMatcher adminRequestMatcher;

    private final JwtAuthenticationProvider jwtAuthenticationProvider;
    private final JwtParseUtil jwtParseUtil;

    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    @PostConstruct
    void init() {
        var requestMatcher =
                PathPatternRequestMatcher.withDefaults().basePath("/");

        permitAllRequestMatcher = new OrRequestMatcher(
                //requestMatcher.matcher("/**"),
                requestMatcher.matcher(HttpMethod.GET, "/swagger-ui/**"),
                requestMatcher.matcher(HttpMethod.GET, "/v3/api-docs/**"),
                requestMatcher.matcher(HttpMethod.GET, "/favicon.ico"),
                requestMatcher.matcher("/auth/**"),
                requestMatcher.matcher(HttpMethod.GET, "/api/v1/posts/**"),
                requestMatcher.matcher(HttpMethod.GET, "/api/v1/comments/*/replies"),
                requestMatcher.matcher("/actuator/**")
        );
        // 인증이 필요한 요청
        authenticatedRequestMatcher = new OrRequestMatcher(
                //requestMatcher.matcher("/**"),
                requestMatcher.matcher(HttpMethod.GET, "/me"),

                // 댓글 관련
                requestMatcher.matcher(HttpMethod.PUT, "/api/v1/comments/**"),
                requestMatcher.matcher(HttpMethod.DELETE, "/api/v1/comments/**"),
                requestMatcher.matcher(HttpMethod.GET, "/api/v1/comments/me"),

                // 게시물 관련
                requestMatcher.matcher(HttpMethod.POST, "/api/v1/posts/**"),
                requestMatcher.matcher(HttpMethod.PUT, "/api/v1/posts/**"),
                requestMatcher.matcher(HttpMethod.DELETE, "/api/v1/posts/**"),

                // 가맹점 관련
                requestMatcher.matcher(HttpMethod.POST, "/api/v1/merchants/**")
        );
        adminRequestMatcher = new OrRequestMatcher(
                requestMatcher.matcher(HttpMethod.GET, "/admin/**")
        );
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(
                jwtAuthenticationProvider
        );
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        RequestMatcher skipEndPoints = permitAllRequestMatcher;
        return new JwtAuthenticationFilter(
                skipEndPoints,
                jwtParseUtil,
                authenticationManager(),
                customAuthenticationEntryPoint
        );
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors((registry) -> registry.configurationSource(corsConfigurationSource()))
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .rememberMe(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .sessionManagement(
                        a -> a.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                .authorizeHttpRequests(
                        configurer -> configurer
                                .requestMatchers(permitAllRequestMatcher)
                                .permitAll()
                                .requestMatchers(authenticatedRequestMatcher)
                                .authenticated()
                                .requestMatchers(adminRequestMatcher)
                                .hasRole("ADMIN")
                )

                .addFilterAfter(jwtAuthenticationFilter(), LogoutFilter.class)

                .exceptionHandling(
                        configurer -> configurer
                                .authenticationEntryPoint(customAuthenticationEntryPoint)
                                .accessDeniedHandler(customAccessDeniedHandler)
                )

                .build();
    }


    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.addAllowedOriginPattern("*");
        configuration.addAllowedMethod("*");
        configuration.addAllowedHeader("*");
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}