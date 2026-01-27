package com.payper.server.security;

import com.payper.server.auth.exception.UserAuthenticationException;
import com.payper.server.global.response.ErrorCode;
import com.payper.server.user.UserRepository;
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
                .orElseThrow(() -> new UserAuthenticationException(ErrorCode.USER_NOTFOUND));

        if (!user.isActive()) {
            throw new UserAuthenticationException(ErrorCode.USER_NOTFOUND);
        }
        return user;
    }
}
