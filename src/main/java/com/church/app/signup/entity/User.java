package com.church.app.signup.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "user")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {
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

    public User(String loginID, String encodedPassword, String role, String name){
        this.loginID = loginID;
        this.password = encodedPassword;
        this.role = role;
        this.name = name;
        this.createdat = LocalDateTime.now();
    }
}
