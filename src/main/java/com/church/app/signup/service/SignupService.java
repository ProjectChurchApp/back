package com.church.app.signup.service;

import com.church.app.signup.dto.SignupRequest;
import com.church.app.signup.entity.Signup;
import com.church.app.signup.repository.SignupRepository;
import com.church.app.signup.utils.PasswordEncoderBCrypt;
import com.church.app.signup.utils.PasswordValidator;
import org.springframework.stereotype.Service;

@Service
public class SignupService {
    private final SignupRepository signupRepository;
    private final PasswordValidator passwordValidator;
    private final PasswordEncoderBCrypt passwordEncoderBCrypt;

    public SignupService(SignupRepository signupRepository, PasswordValidator passwordValidator, PasswordEncoderBCrypt passwordEncoderBCrypt){
        this.signupRepository = signupRepository;
        this.passwordValidator = passwordValidator;
        this.passwordEncoderBCrypt = passwordEncoderBCrypt;
    }

    public void signup(SignupRequest signupRequest){
        String loginID = signupRequest.loginID();
        String password = signupRequest.password();
        String role = signupRequest.role();

        if(signupRepository.existsByloginID(loginID)){
            throw new IllegalArgumentException(("이미 존재하는 아이디입니다 : " + loginID));
        }

        passwordValidator.validate(signupRequest.password());

        String encodedpassword = passwordEncoderBCrypt.encode(signupRequest.password());

        SignupRequest encodedRequest = new SignupRequest(signupRequest.loginID(), encodedpassword, signupRequest.role(), signupRequest.name());

        signupRepository.save(new Signup(encodedRequest));
    }
}
