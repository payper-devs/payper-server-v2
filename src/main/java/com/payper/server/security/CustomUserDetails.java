package com.payper.server.security;

import com.payper.server.user.entity.User;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {
    private final User user;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(
                new SimpleGrantedAuthority(
                        "ROLE_" + user.getUserRole().name()
                )
        );
    }

    @Override
    public @Nullable String getPassword() {
        return "";
    }

    @Override
    public String getUsername() {
        return user.getId().toString();
    }

    public Long getId() {
        return user.getId();
    }

    @Override
    public boolean isEnabled() {
        return user.isActive();
    }
}
