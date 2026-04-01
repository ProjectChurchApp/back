package com.church.app.notification.repository;

import com.church.app.notification.entity.PushToken;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PushTokenRepository extends JpaRepository<PushToken, Long> {
    Optional<PushToken> findByToken(String token);
    void deleteByToken(String token);
}
