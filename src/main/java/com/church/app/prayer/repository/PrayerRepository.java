package com.church.app.prayer.repository;

import com.church.app.connection.entity.PastorConnection;
import com.church.app.prayer.entity.Prayer;
import com.church.app.signup.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PrayerRepository extends JpaRepository<Prayer, Long> {

    // 나의기도: 본인 글 전체(모든 단계)
    List<Prayer> findAllByUserOrderByCreatedDateDesc(User user);

    // 목사: 자신에게 연결된(APPROVED) 성도가 공유(SHARED_WITH_PASTOR)한 기도만
    @Query("SELECT p FROM Prayer p " +
            "WHERE p.boardStage = :stage " +
            "AND p.user.loginID IN (" +
            "  SELECT c.member.loginID FROM PastorConnection c " +
            "  WHERE c.pastor.loginID = :pastorLoginID AND c.status = :connectionStatus" +
            ") ORDER BY p.createdDate DESC")
    List<Prayer> findAllSharedWithPastor(@Param("pastorLoginID") String pastorLoginID,
                                          @Param("stage") Prayer.BoardStage stage,
                                          @Param("connectionStatus") PastorConnection.Status connectionStatus);

    // 중보기도 게시판: 승격된 글 전체 (모든 인증 사용자 열람 가능)
    List<Prayer> findAllByBoardStageOrderByCreatedDateDesc(Prayer.BoardStage boardStage);
}
