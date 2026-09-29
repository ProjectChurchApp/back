package com.church.app.prayer.repository;

import com.church.app.prayer.entity.Prayer;
import com.church.app.prayer.entity.PrayerPrayLog;
import com.church.app.signup.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PrayerPrayLogRepository extends JpaRepository<PrayerPrayLog, Long> {
    boolean existsByPrayerAndUser(Prayer prayer, User user);
    void deleteAllByPrayer(Prayer prayer);
}
