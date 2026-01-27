package com.payper.server.user;

import com.payper.server.auth.exception.UserAuthenticationException;
import com.payper.server.auth.util.OAuthUserInfo;
import com.payper.server.global.response.ErrorCode;
import com.payper.server.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    private final UserRepository userRepository;

    public User save(final User user) {
        validateDuplicate(user);

        return userRepository.save(user);
    }

    private void validateDuplicate(final User user) {
        Optional<User> findUser = switch (user.getAuthType()) {
            case KAKAO -> userRepository.findByOauthIdAndActive(user.getOauthId(), true);
            default ->
                    throw new IllegalArgumentException("Unsupported AuthType for duplicate validation: " + user.getAuthType());
        };

        if (findUser.isPresent()) {
            throw new UserAuthenticationException(ErrorCode.USER_DUPLICATE);
        }
    }

    public User getActiveOAuthUser(OAuthUserInfo oAuthUserInfo) {
        User user =
                userRepository.findByOauthIdAndAuthType(
                                oAuthUserInfo.getOauthId(),
                                oAuthUserInfo.getAuthType()
                        )
                        .orElseThrow(() -> new UserAuthenticationException(ErrorCode.USER_NOTFOUND));

        if (!user.isActive()) {
            throw new UserAuthenticationException(ErrorCode.USER_INACTIVE);
        }
        return user;
    }

    public void delete(final User user) {
        userRepository.updateActiveById(false, user.getId());
    }
}
