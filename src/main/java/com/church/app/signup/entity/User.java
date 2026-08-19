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

    @Convert(converter = RoleConverter.class)
    @Column(name = "role")
    private Role role;

    @Column(name = "name")
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "account_status")
    private AccountStatus accountStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by")
    private User approvedBy;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "createdat")
    private LocalDateTime createdat;

    public User(String loginID, String encodedPassword, Role role, String name){
        this.loginID = loginID;
        this.password = encodedPassword;
        this.role = role;
        this.name = name;
        this.createdat = LocalDateTime.now();
        this.accountStatus = (role == Role.PASTOR)
                ? AccountStatus.PENDING_PASTOR_APPROVAL
                : AccountStatus.ACTIVE;
    }

    public boolean isActive() {
        return this.accountStatus == AccountStatus.ACTIVE;
    }

    public void approve(User approver) {
        this.accountStatus = AccountStatus.ACTIVE;
        this.approvedBy = approver;
        this.approvedAt = LocalDateTime.now();
    }

    public void reject(User approver) {
        this.accountStatus = AccountStatus.REJECTED;
        this.approvedBy = approver;
        this.approvedAt = LocalDateTime.now();
    }
}
