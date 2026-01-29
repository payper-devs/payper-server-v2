package com.payper.server.auth.jwt;

import com.payper.server.auth.jwt.entity.RefreshTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Long> {

    int deleteByUserIdentifier(String userIdentifier);

    Optional<RefreshTokenEntity> findByHashedRefreshToken(String refreshToken);
}
