package com.church.app.notification.entity;

import com.church.app.signup.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "push_token")
public class PushToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "push_token_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(unique = true)
    private String token;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    protected PushToken() {}

    public PushToken(User user, String token) {
        this.user = user;
        this.token = token;
        this.createdDate = LocalDateTime.now();
    }
}
