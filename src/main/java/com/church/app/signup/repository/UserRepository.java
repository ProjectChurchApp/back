package com.church.app.signup.repository;

import com.church.app.signup.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByLoginID(String loginID);

    boolean existsByloginID(String loginID);
}
