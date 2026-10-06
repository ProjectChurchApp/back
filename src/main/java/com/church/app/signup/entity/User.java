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

    /**
     * 탈퇴 처리. 쓰던 아이디를 비우고 이름을 가린다.
     * 기도와 댓글은 그대로 두어 함께 기도하던 사람들의 기록이 사라지지 않게 한다.
     */
    public void withdraw(String freedLoginID, String unusablePassword) {
        this.loginID = freedLoginID;
        this.name = "탈퇴한 사용자";
        this.password = unusablePassword;
        this.accountStatus = AccountStatus.WITHDRAWN;
    }

    /** 관리자가 임시 비밀번호를 발급할 때 쓴다. 이미 암호화된 값을 받는다. */
    public void changePassword(String encodedPassword) {
        this.password = encodedPassword;
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
