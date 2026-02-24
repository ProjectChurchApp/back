package com.church.app.Security.login.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "refresh_token")
@Getter
@NoArgsConstructor
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "refresh_id")
    private int refresh_id;

    @Column(name = "token", nullable = false, unique = true, length = 100)
    private String token;

    @Column(name = "loginID", nullable = false)
    private String loginID;

    @Column(name = "expiry_date", nullable = false)
    private LocalDateTime expiryDate;

    @Column(name = "used", nullable = false)
    private boolean used = false;

    @Column(name = "revoked", nullable = false)
    private boolean revoked = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public RefreshToken(String token, String loginID, LocalDateTime expiryDate) {
        this.token = token;
        this.loginID = loginID;
        this.expiryDate = expiryDate;
    }

    public void markAsUsed() {
        this.used = true;
    }

    public void revoke() {
        this.revoked = true;
    }


    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expiryDate);
    }

    public boolean isValid() {
        return !used && !revoked && !isExpired();
    }
}