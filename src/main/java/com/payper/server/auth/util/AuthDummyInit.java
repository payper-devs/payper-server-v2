package com.payper.server.auth.util;

import com.payper.server.auth.AuthService;
import com.payper.server.user.entity.AuthType;
import com.payper.server.user.entity.User;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthDummyInit implements ApplicationRunner {
    private final AuthService authService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("Auth 더미 데이터 초기화 중");

        OAuthUserInfo dummyOAuth1=new OAuthUserInfo(
                "최미나수",
                "9999",
                AuthType.KAKAO
        );
        OAuthUserInfo dummyOAuth2=new OAuthUserInfo(
                "김고은",
                "8888",
                AuthType.KAKAO
        );
        OAuthUserInfo dummyOAuth3=new OAuthUserInfo(
                "김민지",
                "7777",
                AuthType.KAKAO
        );
        OAuthUserInfo adminDummyOAuth1=new OAuthUserInfo(
                "관리자",
                "2222",
                AuthType.KAKAO
        );

        User dummyUser1 = authService.findOrEnrollOAuthUser(dummyOAuth1);
        User dummyUser2 = authService.findOrEnrollOAuthUser(dummyOAuth2);
        User dummyUser3 = authService.findOrEnrollOAuthUser(dummyOAuth3);
        User dummyUser4 = authService.findOrEnrollOAuthAdminUser(adminDummyOAuth1);

        String accessToken1 = authService.enrollNewAuthTokens(dummyUser1, null);
        String accessToken2 = authService.enrollNewAuthTokens(dummyUser2, null);
        String accessToken3 = authService.enrollNewAuthTokens(dummyUser3, null);
        String accessToken4 = authService.enrollNewAuthTokens(dummyUser4, null);

        log.info("du1: name = {}, at = {}",dummyUser1.getName(),accessToken1);
        log.info("du2: name = {}, at = {}",dummyUser2.getName(),accessToken2);
        log.info("du3: name = {}, at = {}",dummyUser3.getName(),accessToken3);
        log.info("admin du1: name = {}, at = {}",dummyUser4.getName(),accessToken4);
    }


}
