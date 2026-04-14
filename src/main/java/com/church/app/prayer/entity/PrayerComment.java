package com.church.app.prayer.entity;

import com.church.app.signup.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "prayer_comment")
public class PrayerComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "prayer_comment_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prayer_id")
    private Prayer prayer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(columnDefinition = "TEXT")
    private String contents;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

    protected PrayerComment() {}

    public PrayerComment(Prayer prayer, User user, String contents) {
        this.prayer = prayer;
        this.user = user;
        this.contents = contents;
        this.createdDate = LocalDateTime.now();
    }

    public void update(String contents) {
        this.contents = contents;
        this.updatedDate = LocalDateTime.now();
    }
}