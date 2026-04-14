package com.church.app.notification.repository;

import com.church.app.notification.entity.PushToken;
import com.church.app.signup.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PushTokenRepository extends JpaRepository<PushToken, Long> {
    Optional<PushToken> findByToken(String token);
    void deleteByToken(String token);
    List<PushToken> findAllByUser(User user);
}
