package com.church.app.signup.service;

import com.church.app.signup.dto.SignupRequest;
import com.church.app.signup.entity.User;
import com.church.app.signup.repository.UserRepository;
import com.church.app.signup.utils.PasswordEncoderBCrypt;
import com.church.app.signup.utils.PasswordValidator;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class SignupService {
    private final UserRepository userRepository;
    private final PasswordValidator passwordValidator;
    private final PasswordEncoder passwordEncoder;

    public SignupService(UserRepository userRepository, PasswordValidator passwordValidator, PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.passwordValidator = passwordValidator;
        this.passwordEncoder = passwordEncoder;
    }

    public void signup(SignupRequest signupRequest){
        String loginID = signupRequest.loginID();
        String password = signupRequest.password();
        String role = signupRequest.role();

        if(userRepository.existsByloginID(loginID)){
            throw new IllegalArgumentException(("이미 존재하는 아이디입니다 : " + loginID));
        }

        passwordValidator.validate(signupRequest.password());

        String encodedpassword = passwordEncoder.encode(signupRequest.password());

        userRepository.save(new User(signupRequest.loginID(), encodedpassword, signupRequest.role(), signupRequest.name()));
    }
}
