package com.payper.server.user.repository;

import com.payper.server.user.entity.AuthType;
import com.payper.server.user.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUserIdentifier(String userIdentifier);

    @Query("update User u set u.active = :active where u.id = :id")
    @Modifying
    int updateActiveById(boolean active, Long id);

    Optional<User> findByOauthIdAndActive(String oauthId, boolean active);

    Optional<User> findByOauthIdAndAuthType(String oauthId, AuthType authType);
}
