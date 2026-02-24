package com.church.app.Security.login.repository;

import com.church.app.Security.login.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    List<RefreshToken> findByLoginID(String loginID);

    void deleteByLoginID(String loginID);

    void deleteByToken(String token);

    boolean existsByLoginID(String loginID);
}
