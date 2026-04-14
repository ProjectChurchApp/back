package com.church.app.prayer.repository;

import com.church.app.prayer.entity.PrayerComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PrayerCommentRepository extends JpaRepository<PrayerComment, Long> {
    List<PrayerComment> findAllByPrayerIdOrderByCreatedDateAsc(Long prayerId);
}