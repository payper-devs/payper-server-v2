package com.payper.server;

import com.payper.server.auth.jwt.RefreshTokenEntity;
import com.payper.server.auth.jwt.RefreshTokenRepository;
import com.payper.server.security.CustomUserDetails;
import com.payper.server.user.UserRepository;
import com.payper.server.user.entity.AuthType;
import com.payper.server.user.entity.User;
import com.payper.server.user.entity.UserRole;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Transactional
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // <-- MySQL 그대로 사용
class UserAndRefreshTokenJpaTest {

    @Autowired UserRepository userRepository;
    @Autowired RefreshTokenRepository refreshTokenRepository;
    @Autowired EntityManager em;

    @Test
    @DisplayName("User 저장 후 userIdentifier로 조회가 된다")
    void saveUser_and_findByUserIdentifier() {
        // given
        User user = User.create(AuthType.KAKAO, "경현", "kakao-123", UserRole.USER, true);

        // when
        User saved = userRepository.save(user);
        em.flush();
        em.clear();

        // then
        assertThat(saved.getId()).isNotNull();

        User found = userRepository.findByUserIdentifier(saved.getUserIdentifier())
                .orElseThrow();

        assertThat(found.getId()).isEqualTo(saved.getId());
        assertThat(found.getOauthId()).isEqualTo("kakao-123");
        assertThat(found.getAuthType()).isEqualTo(AuthType.KAKAO);
        assertThat(found.getUserRole()).isEqualTo(UserRole.USER);
        assertThat(found.isActive()).isTrue();
    }

    @Test
    @DisplayName("oauthId + active로 User 조회가 된다")
    void findByOauthIdAndActive() {
        // given
        userRepository.save(User.create(AuthType.KAKAO, "경현", "kakao-999", UserRole.USER, true));
        em.flush();
        em.clear();

        // when
        User found = userRepository.findByOauthIdAndActive("kakao-999", true)
                .orElseThrow();

        // then
        assertThat(found.getOauthId()).isEqualTo("kakao-999");
        assertThat(found.isActive()).isTrue();
    }

    @Test
    @DisplayName("@Modifying updateActiveById로 active 변경이 DB에 반영된다")
    void updateActiveById_updatesRow() {
        // given
        User saved = userRepository.save(User.create(AuthType.KAKAO, "경현", "kakao-777", UserRole.USER, true));
        em.flush();
        em.clear();

        // when
        int updated = userRepository.updateActiveById(false, saved.getId());
        em.flush();
        em.clear();

        // then
        assertThat(updated).isEqualTo(1);

        User reloaded = userRepository.findById(saved.getId()).orElseThrow();
        assertThat(reloaded.isActive()).isFalse();
    }

    @Test
    @DisplayName("RefreshToken 저장 후 hashedRefreshToken으로 조회가 된다")
    void saveRefreshToken_and_findByHashedRefreshToken() {
        // given
        User user = userRepository.save(User.create(AuthType.KAKAO, "경현", "kakao-rt", UserRole.USER, true));
        String userIdentifier = user.getUserIdentifier();

        RefreshTokenEntity rt = RefreshTokenEntity.create(userIdentifier, "hashed-rt-123");

        // when
        refreshTokenRepository.save(rt);
        em.flush();
        em.clear();

        // then (repo 메서드가 Optional이 아니라 null 가능)
        RefreshTokenEntity found = refreshTokenRepository.findByHashedRefreshToken("hashed-rt-123").get();
        assertThat(found).isNotNull();
        assertThat(found.getUserIdentifier()).isEqualTo(userIdentifier);
        assertThat(found.getHashedRefreshToken()).isEqualTo("hashed-rt-123");
    }

    @Test
    @DisplayName("RefreshToken deleteByUserIdentifier가 정상 동작한다")
    void deleteRefreshToken_byUserIdentifier() {
        // given
        User user = userRepository.save(User.create(AuthType.KAKAO, "경현", "kakao-del", UserRole.USER, true));
        String userIdentifier = user.getUserIdentifier();

        refreshTokenRepository.save(RefreshTokenEntity.create(userIdentifier, "hashed-del-1"));
        em.flush();
        em.clear();

        // when
        int deleted = refreshTokenRepository.deleteByUserIdentifier(userIdentifier);
        em.flush();
        em.clear();

        // then
        assertThat(deleted).isEqualTo(1);
        assertThat(refreshTokenRepository.findByHashedRefreshToken("hashed-del-1")).isNull();
    }

    @Test
    @DisplayName("CustomUserDetails로 User -> Principal 변환이 가능하다")
    void customUserDetails_canConvertUserToPrincipal() {
        // given (id 생성 필요)
        User saved = userRepository.save(
                User.create(AuthType.KAKAO, "경현", "kakao-principal", UserRole.ADMIN, true)
        );
        em.flush();
        em.clear();

        User reloaded = userRepository.findById(saved.getId()).orElseThrow();

        // when
        CustomUserDetails principal = new CustomUserDetails(reloaded);

        // then
        assertThat(principal.getUsername()).isEqualTo(reloaded.getId().toString());
        assertThat(principal.isEnabled()).isTrue();
        assertThat(principal.getPassword()).isEmpty();
        assertThat(principal.getAuthorities())
                .extracting(ga -> ga.getAuthority())
                .containsExactly("ROLE_ADMIN");
    }
}
