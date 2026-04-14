package com.church.app.comment.repository;

import com.church.app.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByBoardIdOrderByCreatedDateAsc(Long boardId);
    void deleteAllByBoardId(Long boardId);
}