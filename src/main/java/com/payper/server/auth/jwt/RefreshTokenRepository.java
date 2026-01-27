package com.payper.server.auth.jwt;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Long> {

    int deleteByUserIdentifier(String memberIdentifier);

    RefreshTokenEntity findByHashedRefreshToken(String refreshToken);
}
