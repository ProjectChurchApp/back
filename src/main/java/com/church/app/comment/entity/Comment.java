package com.church.app.comment.entity;

import com.church.app.board.entity.Board;
import com.church.app.signup.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "comment")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(columnDefinition = "TEXT")
    private String contents;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

    protected Comment() {}

    public Comment(Board board, User user, String contents) {
        this.board = board;
        this.user = user;
        this.contents = contents;
        this.createdDate = LocalDateTime.now();
    }

    public void update(String contents) {
        this.contents = contents;
        this.updatedDate = LocalDateTime.now();
    }
}