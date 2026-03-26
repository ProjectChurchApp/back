package com.church.app.board.repository;

import com.church.app.board.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Long> {

    // 상태별 + 최신순
    List<Board> findAllByStatusOrderByCreatedDateDesc(Board.Status status);

    // 전체 최신순
    List<Board> findAllByOrderByCreatedDateDesc();
}
