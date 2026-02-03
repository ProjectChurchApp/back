package com.church.app.signup.repository;

import com.church.app.signup.entity.Signup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SignupRepository extends JpaRepository<Signup, Integer> {

    boolean existsByloginID(String loginID);
}
