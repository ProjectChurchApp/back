package com.church.app.prayer.repository;

import com.church.app.prayer.entity.Prayer;
import com.church.app.prayer.entity.PrayerPrayLog;
import com.church.app.signup.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PrayerPrayLogRepository extends JpaRepository<PrayerPrayLog, Long> {
    Optional<PrayerPrayLog> findByPrayerAndUser(Prayer prayer, User user);
    boolean existsByPrayerAndUser(Prayer prayer, User user);
}
