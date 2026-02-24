package com.church.app.signup.repository;

import com.church.app.signup.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {

    boolean existsByloginID(String loginID);
}
