package com.payper.server.auth.util;

import com.payper.server.auth.exception.OAuthException;
import com.payper.server.global.response.ErrorCode;
import com.payper.server.user.entity.AuthType;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
public class KakaoOAuthUtilImpl implements OAuthUtil {
    private static final String PROPERTY_KEYS = "[\"kakao_account.profile\"]";

    private RestClient kakaoRestClient;
    private final ObjectMapper objectMapper;

    @PostConstruct
    protected void init() {
        kakaoRestClient=RestClient.builder()
                .baseUrl("https://kapi.kakao.com")
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @Override
    public OAuthUserInfo getUserInfoFromOAuthToken(String oAuthToken) {
        String body = kakaoRestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v2/user/me")
                        .queryParam("property_keys", PROPERTY_KEYS)
                        .build())
                .headers(headers -> headers.setBearerAuth(oAuthToken))
                .retrieve()
                .onStatus(
                        HttpStatusCode::isError,
                        (request, response) ->
                        {
                            throw new OAuthException(ErrorCode.OAUTH_RESOURCE_ERROR);
                        }
                )
                .body(String.class);

        try {
            JsonNode json = objectMapper.readTree(body);

            String name = json.path("kakao_account")
                    .path("profile")
                    .path("nickname")
                    .asString();

            String kakaoId = json.path("id").asString();

            return new OAuthUserInfo(name, kakaoId, AuthType.KAKAO);
        } catch (Exception e) {
            throw new OAuthException(ErrorCode.OAUTH_RESOURCE_ERROR);
        }
    }
}
