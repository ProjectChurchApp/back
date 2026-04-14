package com.church.app.prayer.entity;

import com.church.app.signup.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "prayer")
public class Prayer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "prayer_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String contents;

    public enum Visibility {
        PRIVATE, PASTOR
    }

    @Enumerated(EnumType.STRING)
    private Visibility visibility;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

    protected Prayer() {}

    public Prayer(User user, String title, String contents, Visibility visibility) {
        this.user = user;
        this.title = title;
        this.contents = contents;
        this.visibility = visibility;
        this.createdDate = LocalDateTime.now();
    }

    public void update(String title, String contents, Visibility visibility) {
        this.title = title;
        this.contents = contents;
        this.visibility = visibility;
        this.updatedDate = LocalDateTime.now();
    }
}