package com.church.app.connection.entity;

import com.church.app.signup.entity.User;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "pastor_connection")
public class PastorConnection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "connection_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private User member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pastor_id")
    private User pastor;

    public enum Status {
        PENDING, APPROVED, REJECTED
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status;

    @Column(name = "requested_at")
    private LocalDateTime requestedAt;

    @Column(name = "responded_at")
    private LocalDateTime respondedAt;

    protected PastorConnection() {}

    public PastorConnection(User member, User pastor) {
        this.member = member;
        this.pastor = pastor;
        this.status = Status.PENDING;
        this.requestedAt = LocalDateTime.now();
    }

    public void approve() {
        this.status = Status.APPROVED;
        this.respondedAt = LocalDateTime.now();
    }

    public void reject() {
        this.status = Status.REJECTED;
        this.respondedAt = LocalDateTime.now();
    }
}
