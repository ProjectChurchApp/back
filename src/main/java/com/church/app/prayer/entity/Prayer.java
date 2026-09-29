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

    public enum BoardStage {
        PERSONAL, SHARED_WITH_PASTOR, INTERCESSORY
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "board_stage")
    private BoardStage boardStage;

    public enum Status {
        PRAYING, ANSWERED, CLOSED
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status;

    @Column(name = "status_changed_at")
    private LocalDateTime statusChangedAt;

    @Column(name = "prayer_count")
    private int prayerCount;

    @Column(name = "intercessory_requested")
    private boolean intercessoryRequested;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "promoted_by")
    private User promotedBy;

    @Column(name = "promoted_at")
    private LocalDateTime promotedAt;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

    protected Prayer() {}

    public Prayer(User user, String title, String contents, Visibility visibility, boolean intercessoryRequested) {
        this.user = user;
        this.title = title;
        this.contents = contents;
        this.visibility = visibility;
        this.intercessoryRequested = visibility == Visibility.PASTOR && intercessoryRequested;
        this.boardStage = toBoardStage(visibility);
        this.status = Status.PRAYING;
        this.prayerCount = 0;
        this.createdDate = LocalDateTime.now();
    }

    private static BoardStage toBoardStage(Visibility visibility) {
        return visibility == Visibility.PASTOR ? BoardStage.SHARED_WITH_PASTOR : BoardStage.PERSONAL;
    }

    public boolean isPromoted() {
        return this.boardStage == BoardStage.INTERCESSORY;
    }

    public void update(String title, String contents, Visibility visibility, boolean intercessoryRequested) {
        this.title = title;
        this.contents = contents;
        this.visibility = visibility;
        if (!isPromoted()) {
            this.boardStage = toBoardStage(visibility);
            this.intercessoryRequested = visibility == Visibility.PASTOR && intercessoryRequested;
        }
        this.updatedDate = LocalDateTime.now();
    }

    public void changeStatus(Status status) {
        this.status = status;
        this.statusChangedAt = status == Status.PRAYING ? null : LocalDateTime.now();
        this.updatedDate = LocalDateTime.now();
    }

    public void promote(User pastor) {
        this.boardStage = BoardStage.INTERCESSORY;
        this.promotedBy = pastor;
        this.promotedAt = LocalDateTime.now();
    }

    public void incrementPrayerCount() {
        this.prayerCount++;
    }
}
