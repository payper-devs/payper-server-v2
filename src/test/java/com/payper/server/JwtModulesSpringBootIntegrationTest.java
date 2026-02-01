package com.payper.server;

import com.payper.server.auth.AuthException;
import com.payper.server.auth.jwt.RefreshTokenRepository;
import com.payper.server.auth.jwt.entity.RefreshTokenEntity;
import com.payper.server.auth.jwt.entity.JwtType;
import com.payper.server.auth.jwt.util.JwtParseUtil;
import com.payper.server.auth.jwt.util.JwtProperties;
import com.payper.server.auth.jwt.util.JwtRefreshTokenUtil;
import com.payper.server.auth.jwt.util.JwtTokenUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class JwtModulesSpringBootIntegrationTest {

    @Autowired
    JwtProperties jwtProperties;

    @Autowired
    JwtTokenUtil jwtTokenUtil;
    @Autowired
    JwtParseUtil jwtParseUtil;

    @Autowired
    JwtRefreshTokenUtil jwtRefreshTokenUtil;
    @Autowired
    RefreshTokenRepository refreshTokenRepository;

    /**
     * ✅ 이 테스트는 "실제 MySQL 연동"이므로
     * - 각 테스트가 서로 간섭하지 않게 매번 DB를 비우고
     * - @Transactional이 적용된 테스트라면(기본 롤백)에도,
     * 내부 util이 REQUIRES_NEW로 flush/commit을 때리며 남길 수 있어
     * BeforeEach에서 강제로 정리하는 방식이 안전함.
     */
    @BeforeEach
    void cleanDb() {
        refreshTokenRepository.deleteAll();
        refreshTokenRepository.flush();
    }

    // ✅ JWT iat/exp는 라이브러리/표준 때문에 초 단위로 잘릴 수 있어 테스트도 초 단위로 비교
    private static long epochSecond(Date d) {
        return d.getTime() / 1000;
    }

    @Test
    @DisplayName("ACCESS 토큰 발급 후 subject/iat/exp/type 파싱이 된다 (iat/exp는 초 단위 비교)")
    void issueAndParse_accessToken() {
        // given
        String userIdentifier = "user-123";
        Date now = new Date();
        String accessToken = jwtTokenUtil.generateJwtToken(JwtType.ACCESS, now, userIdentifier);

        // when
        String sub = jwtParseUtil.getUserIdentifier(accessToken);
        Date iat = jwtParseUtil.getIssuedAt(accessToken);
        Date exp = jwtParseUtil.getExpiresAt(accessToken);
        JwtType type = jwtParseUtil.getJwtType(accessToken);

        // then
        assertThat(sub).isEqualTo(userIdentifier);
        assertThat(type).isEqualTo(JwtType.ACCESS);

        assertThat(epochSecond(iat)).isEqualTo(epochSecond(now));

        Date expectedExp = new Date(now.getTime() + jwtProperties.getAccessTokenTime());
        assertThat(epochSecond(exp)).isEqualTo(epochSecond(expectedExp));
    }

    @Test
    @DisplayName("REFRESH 토큰 발급 후 subject/iat/exp/type 파싱이 된다 (iat/exp는 초 단위 비교)")
    void issueAndParse_refreshToken() {
        // given
        String userIdentifier = "user-999";
        Date now = new Date();
        String refreshToken = jwtTokenUtil.generateJwtToken(JwtType.REFRESH, now, userIdentifier);

        // when
        String sub = jwtParseUtil.getUserIdentifier(refreshToken);
        Date iat = jwtParseUtil.getIssuedAt(refreshToken);
        Date exp = jwtParseUtil.getExpiresAt(refreshToken);
        JwtType type = jwtParseUtil.getJwtType(refreshToken);

        // then
        assertThat(sub).isEqualTo(userIdentifier);
        assertThat(type).isEqualTo(JwtType.REFRESH);

        assertThat(epochSecond(iat)).isEqualTo(epochSecond(now));

        Date expectedExp = new Date(now.getTime() + jwtProperties.getRefreshTokenTime());
        assertThat(epochSecond(exp)).isEqualTo(epochSecond(expectedExp));
    }

    @Test
    @DisplayName("만료된 토큰은 JwtValidAuthenticationException이 발생한다")
    void expiredToken_throws() {
        // given: 1970 기준 발급 → 현재 시점에서 무조건 만료
        String userIdentifier = "user-expired";
        Date oldNow = new Date(0L);
        String expired = jwtTokenUtil.generateJwtToken(JwtType.ACCESS, oldNow, userIdentifier);

        // when & then
        assertThatThrownBy(() -> jwtParseUtil.getUserIdentifier(expired))
                .isInstanceOf(AuthException.class);
    }

    @Test
    @DisplayName("Authorization 헤더에서 Bearer 토큰을 추출한다")
    void extractFromRequest_bearer() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer abc.def.ghi");

        assertThat(jwtParseUtil.extractJwtTokenFromRequest(request))
                .isEqualTo("abc.def.ghi");
    }

    /**
     * ✅ DB 통합 플로우 테스트들은 '테스트 메서드 단위 트랜잭션' 안에서 실행되도록 @Transactional 부여
     * - 이 테스트 클래스 전체에 @Transactional을 걸지 않는 이유:
     * util 내부에 REQUIRES_NEW가 섞여 있으면 테스트 트랜잭션 롤백으로도 데이터가 남을 수 있어서
     * 오히려 오해를 만들기 쉬움.
     * - 대신: 각 테스트 시작 전 cleanDb()로 완전 격리
     */
    @Test
    @Transactional
    @DisplayName("같은 raw refreshToken은 항상 같은 해시로 엔티티가 생성된다")
    void refresh_sameToken_sameHash() {
        String userIdentifier = "user-1";
        String raw = "raw-refresh-token-value";

        RefreshTokenEntity e1 = jwtRefreshTokenUtil.generateRefreshTokenEntity(userIdentifier, raw);
        RefreshTokenEntity e2 = jwtRefreshTokenUtil.generateRefreshTokenEntity(userIdentifier, raw);

        assertThat(e1.getUserIdentifier()).isEqualTo(userIdentifier);
        assertThat(e1.getHashedRefreshToken()).isNotBlank();
        assertThat(e1.getHashedRefreshToken()).isEqualTo(e2.getHashedRefreshToken());
    }

    @Test
    @Transactional
    @DisplayName("upsertRefreshTokenEntity는 (user 기준) 기존 토큰을 제거하고 새 토큰만 남긴다")
    void refresh_upsert_replacesExistingTokenForUser() {
        String userIdentifier = "user-upsert";

        // 1) 첫 토큰 업서트
        String raw1 = jwtTokenUtil.generateJwtToken(JwtType.REFRESH, new Date(), userIdentifier);
        RefreshTokenEntity e1 = jwtRefreshTokenUtil.generateRefreshTokenEntity(userIdentifier, raw1);
        jwtRefreshTokenUtil.upsertRefreshTokenEntity(e1);

        assertThat(jwtRefreshTokenUtil.getRefreshTokenEntity(raw1)).isNotEmpty();

        // 2) 두 번째 토큰 업서트 → 첫 토큰은 제거되어야 함
        String raw2 = jwtTokenUtil.generateJwtToken(JwtType.REFRESH, new Date(), userIdentifier);
        RefreshTokenEntity e2 = jwtRefreshTokenUtil.generateRefreshTokenEntity(userIdentifier, raw2);
        jwtRefreshTokenUtil.upsertRefreshTokenEntity(e2);

        assertThat(jwtRefreshTokenUtil.getRefreshTokenEntity(raw1)).isEmpty();

        Optional<RefreshTokenEntity> found2 = jwtRefreshTokenUtil.getRefreshTokenEntity(raw2);
        assertThat(found2).isNotNull();
        found2.ifPresent(
                refreshTokenEntity -> assertThat(refreshTokenEntity.getUserIdentifier()
                ).isEqualTo(userIdentifier));
    }

    @Test
    //@Transactional
    @DisplayName("deleteAllRefreshTokenEntity는 사용자 토큰을 삭제하고, 이후 조회가 null이 된다")
    void refresh_deleteAll_deletesAndThenLookupNull() {
        String userIdentifier = "user-del";

        String raw = jwtTokenUtil.generateJwtToken(JwtType.REFRESH, new Date(), userIdentifier);
        RefreshTokenEntity e = jwtRefreshTokenUtil.generateRefreshTokenEntity(userIdentifier, raw);
        refreshTokenRepository.saveAndFlush(e);

        assertThat(jwtRefreshTokenUtil.getRefreshTokenEntity(raw)).isNotEmpty();

        int deleted = jwtRefreshTokenUtil.deleteAllRefreshTokenEntity(userIdentifier);

        assertThat(deleted).isGreaterThanOrEqualTo(1);
        assertThat(jwtRefreshTokenUtil.getRefreshTokenEntity(raw)).isEmpty();
    }
}
