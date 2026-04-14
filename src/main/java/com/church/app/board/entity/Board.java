package com.church.app.board.entity;

import com.church.app.signup.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "board")
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_id")
    private Long id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String contents;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // 읽음/ 안읽음
    public enum Status {
        UNREAD, READ
    }
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "create_date")
    private LocalDateTime createdDate;

    @Column(name = "update_date")
    private LocalDateTime updatedDate;

    protected Board(){

    }
    public Board(String title, String contents, User user) {
        this.title = title;
        this.contents = contents;
        this.user = user;
        this.status = Status.UNREAD;
        this.createdDate = LocalDateTime.now();
    }
    public void update(String title, String contents) {
        this.title = title;
        this.contents = contents;
    }

    public void markAsRead() {
        this.status = Status.READ;
    }

    @PrePersist
    public void prePersist() {
        this.createdDate = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedDate = LocalDateTime.now();
    }
}

