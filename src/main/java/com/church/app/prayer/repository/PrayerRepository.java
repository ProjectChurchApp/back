package com.church.app.prayer.repository;

import com.church.app.prayer.entity.Prayer;
import com.church.app.signup.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PrayerRepository extends JpaRepository<Prayer, Long> {

    // 내 글 전체 조회
    List<Prayer> findAllByUserOrderByCreatedDateDesc(User user);

    // 목사용: 나만보기 제외하고 전체 조회 (PASTOR 공개글)
    @Query("SELECT p FROM Prayer p WHERE p.visibility = 'PASTOR' OR p.user = :user ORDER BY p.createdDate DESC")
    List<Prayer> findAllVisibleToUser(@Param("user") User user);
}