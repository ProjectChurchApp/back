package com.church.app.comment.controller;

import com.church.app.comment.dto.CommentRequestDto;
import com.church.app.comment.dto.CommentResponseDto;
import com.church.app.comment.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comment")
public class CommentController {

    private final CommentService commentService;

    // 댓글 목록
    @GetMapping("/board/{boardId}")
    public List<CommentResponseDto> getComments(@PathVariable Long boardId) {
        return commentService.getComments(boardId);
    }

    // 댓글 작성
    @PostMapping("/board/{boardId}")
    public String createComment(@PathVariable Long boardId,
                                @RequestBody CommentRequestDto dto,
                                Authentication authentication) {
        commentService.createComment(boardId, dto, authentication.getName());
        return "댓글 작성 완료";
    }

    // 댓글 수정
    @PutMapping("/{commentId}")
    public String updateComment(@PathVariable Long commentId,
                                @RequestBody CommentRequestDto dto,
                                Authentication authentication) {
        commentService.updateComment(commentId, dto, authentication.getName());
        return "댓글 수정 완료";
    }

    // 댓글 삭제
    @DeleteMapping("/{commentId}")
    public String deleteComment(@PathVariable Long commentId,
                                Authentication authentication) {
        commentService.deleteComment(commentId, authentication.getName());
        return "댓글 삭제 완료";
    }
}