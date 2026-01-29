package com.payper.server.auth.jwt;

import com.payper.server.auth.jwt.entity.RefreshTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Long> {

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("delete from RefreshTokenEntity r where r.userIdentifier = :userIdentifier")
    int deleteByUserIdentifier(@Param("userIdentifier") String userIdentifier);

    Optional<RefreshTokenEntity> findByHashedRefreshToken(String refreshToken);
}
