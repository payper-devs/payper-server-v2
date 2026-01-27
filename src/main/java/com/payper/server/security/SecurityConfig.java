package com.payper.server.security;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private RequestMatcher permitAllRequestMatcher;
    private RequestMatcher authenticatedRequestMatcher;
    private RequestMatcher adminRequestMatcher;

    @PostConstruct
    void init() {
        var requestMatcher =
                PathPatternRequestMatcher.withDefaults().basePath("/");

        permitAllRequestMatcher = new OrRequestMatcher(
                requestMatcher.matcher(HttpMethod.GET, "/swagger-ui/**"),
                requestMatcher.matcher(HttpMethod.GET, "/v3/api-docs/**"),
                requestMatcher.matcher(HttpMethod.GET, "/favicon.ico"),
                requestMatcher.matcher("/auth/**")
        );
        authenticatedRequestMatcher = new OrRequestMatcher(
                requestMatcher.matcher(HttpMethod.GET, "/me")
        );
        adminRequestMatcher = new OrRequestMatcher(
                requestMatcher.matcher(HttpMethod.GET, "/admin/**")
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
                                .permitAll()
                                .requestMatchers(adminRequestMatcher)
                                .hasRole("ADMIN")
                                .anyRequest().authenticated()
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