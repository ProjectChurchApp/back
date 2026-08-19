package com.church.app.prayer.entity;

import com.church.app.signup.entity.User;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "prayer_pray_log", uniqueConstraints = @UniqueConstraint(columnNames = {"prayer_id", "user_id"}))
public class PrayerPrayLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pray_log_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prayer_id")
    private Prayer prayer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    protected PrayerPrayLog() {}

    public PrayerPrayLog(Prayer prayer, User user) {
        this.prayer = prayer;
        this.user = user;
        this.createdDate = LocalDateTime.now();
    }
}
