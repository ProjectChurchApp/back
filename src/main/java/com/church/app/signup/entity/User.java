package com.church.app.signup.entity;

import com.church.app.signup.dto.SignupRequest;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "user")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Signup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private int userId;

    @Column(name = "loginID")
    private String loginID;

    @Column(name = "password")
    private String password;

    @Column(name = "role")
    private String role;

    @Column(name = "name")
    private String name;

    @Column(name = "createdat")
    private LocalDateTime createdat;

    public Signup(String loginID, String encodedPassword, String role, String name){
        this.loginID = loginID;
        this.password = encodedPassword;
        this.role = role;
        this.name = name;
        this.createdat = LocalDateTime.now();
    }
}
