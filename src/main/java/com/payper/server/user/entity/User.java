package com.payper.server.user.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false,unique = true,updatable = false)
    private String userIdentifier;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuthType authType;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false,updatable = false)
    private String oauthId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole userRole;

    @Column(nullable = false)
    private boolean active;

    public static User create(
            AuthType authType,
            String name,
            String oauthId,
            UserRole userRole,
            boolean active
    ){
        return User.builder()
                .userIdentifier(UUID.randomUUID().toString())
                .authType(authType)
                .name(name)
                .oauthId(oauthId)
                .userRole(userRole)
                .active(active)
                .build();
    }
}