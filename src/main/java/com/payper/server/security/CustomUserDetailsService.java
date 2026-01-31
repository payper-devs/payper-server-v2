package com.payper.server.security;

import com.payper.server.auth.AuthException;
import com.payper.server.global.response.ErrorCode;
import com.payper.server.user.repository.UserRepository;
import com.payper.server.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String userIdentifier) throws UsernameNotFoundException {
        User activeUser = getActiveUserByUserIdentifier(userIdentifier);
        return new CustomUserDetails(activeUser);
    }

    public User getActiveUserByUserIdentifier(String userIdentifier) {
        User user = userRepository.findByUserIdentifier(userIdentifier)
                .orElseThrow(() -> new AuthException(ErrorCode.USER_NOT_FOUND_AUTH));

        if (!user.isActive()) {
            throw new AuthException(ErrorCode.USER_NOT_FOUND_AUTH);
        }
        return user;
    }
}
