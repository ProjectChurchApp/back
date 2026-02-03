package com.church.app.signup.controller;

import com.church.app.signup.dto.SignupRequest;
import com.church.app.signup.service.SignupService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class SignupController {
    private SignupService signupService;

    public SignupController(SignupService signupService){
        this.signupService = signupService;
    }

    @PostMapping("/signup")
    public ResponseEntity<Void> signup(@RequestBody SignupRequest signupRequest){
        signupService.signup(signupRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
