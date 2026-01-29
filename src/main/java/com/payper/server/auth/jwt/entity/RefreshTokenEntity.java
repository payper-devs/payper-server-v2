package com.payper.server.auth.jwt.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "refresh_token_entity")
public class RefreshTokenEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false,unique = true,updatable = false)
    private String userIdentifier;

    @Column(nullable = false,unique = true, updatable = false)
    private String hashedRefreshToken;

    public static RefreshTokenEntity create(
            String userIdentifier,
            String hashedRefreshToken
    ){
        return RefreshTokenEntity.builder()
                .userIdentifier(userIdentifier)
                .hashedRefreshToken(hashedRefreshToken)
                .build();
    }
}
