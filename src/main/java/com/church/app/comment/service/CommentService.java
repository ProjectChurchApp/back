package com.church.app.comment.service;

import com.church.app.board.entity.Board;
import com.church.app.board.repository.BoardRepository;
import com.church.app.comment.dto.CommentRequestDto;
import com.church.app.comment.dto.CommentResponseDto;
import com.church.app.comment.entity.Comment;
import com.church.app.comment.repository.CommentRepository;
import com.church.app.notification.service.PushNotificationService;
import com.church.app.signup.entity.User;
import com.church.app.signup.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {

    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final PushNotificationService pushNotificationService;

    // 댓글 목록 조회
    public List<CommentResponseDto> getComments(Long boardId) {
        return commentRepository.findAllByBoardIdOrderByCreatedDateAsc(boardId)
                .stream()
                .map(CommentResponseDto::new)
                .toList();
    }

    // 댓글 작성
    public void createComment(Long boardId, CommentRequestDto dto, String loginID) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new RuntimeException("게시글 없음"));

        User user = userRepository.findByLoginID(loginID)
                .orElseThrow(() -> new RuntimeException("유저 없음"));

        commentRepository.save(new Comment(board, user, dto.getContents()));

        // 게시글 작성자 + 댓글 단 사람 모두 수집 (중복 제거, 본인 제외)
        Set<String> notifyTargets = new HashSet<>();

        // 게시글 작성자 추가
        notifyTargets.add(board.getUser().getLoginID());

        // 기존 댓글 작성자들 추가
        commentRepository.findAllByBoardIdOrderByCreatedDateAsc(boardId)
                .forEach(c -> notifyTargets.add(c.getUser().getLoginID()));

        // 본인 제외
        notifyTargets.remove(loginID);

        // 알림 전송
        notifyTargets.forEach(id -> pushNotificationService.sendToUser(
                id,
                "새 댓글 💬",
                user.getName() + ": " + dto.getContents()
        ));
    }

    // 댓글 수정
    public void updateComment(Long commentId, CommentRequestDto dto, String loginID) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("댓글 없음"));

        if (!comment.getUser().getLoginID().equals(loginID)) {
            throw new RuntimeException("수정 권한 없음");
        }

        comment.update(dto.getContents());
    }

    // 댓글 삭제
    public void deleteComment(Long commentId, String loginID) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("댓글 없음"));

        if (!comment.getUser().getLoginID().equals(loginID)) {
            throw new RuntimeException("삭제 권한 없음");
        }

        commentRepository.delete(comment);
    }
}