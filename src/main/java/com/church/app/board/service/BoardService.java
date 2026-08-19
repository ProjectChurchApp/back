package com.church.app.board.service;

import com.church.app.board.dto.BoardRequestDto;
import com.church.app.board.dto.BoardResponseDto;
import com.church.app.board.entity.Board;
import com.church.app.board.repository.BoardRepository;
import com.church.app.comment.repository.CommentRepository;
import com.church.app.common.exception.ForbiddenActionException;
import com.church.app.common.exception.ResourceNotFoundException;
import com.church.app.notification.service.PushNotificationService;
import com.church.app.signup.entity.Role;
import com.church.app.signup.entity.User;
import com.church.app.signup.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BoardService {

    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PushNotificationService pushNotificationService;

    public void createBoard(BoardRequestDto dto, String loginID) {
        User user = findUser(loginID);
        requireActivePastor(user);

        boardRepository.save(new Board(dto.getTitle(), dto.getContents(), user));

        // 게시글 작성 시 전체 푸시 전송
        pushNotificationService.sendToAll(
                "새 게시글 ✉️",
                user.getName() + ": " + dto.getTitle()
        );
    }

    public List<BoardResponseDto> getAllBoardsDesc() {
        return boardRepository.findAllByOrderByCreatedDateDesc()
                .stream()
                .map(BoardResponseDto::new)
                .toList();
    }

    public List<BoardResponseDto> getBoardsByStatus(Board.Status status) {
        return boardRepository.findAllByStatusOrderByCreatedDateDesc(status)
                .stream()
                .map(BoardResponseDto::new)
                .toList();
    }

    public BoardResponseDto getBoard(Long id) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("게시글 없음"));

        if (board.getStatus() == Board.Status.UNREAD) {
            board.markAsRead();
        }

        return new BoardResponseDto(board);
    }

    public void updateBoard(Long id, BoardRequestDto dto, String loginID) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("게시글 없음"));

        requireOwnerOrActivePastor(board, loginID);

        board.update(dto.getTitle(), dto.getContents());
    }

    public void deleteBoard(Long id, String loginID) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("게시글 없음"));

        requireOwnerOrActivePastor(board, loginID);

        commentRepository.deleteAllByBoardId(id);
        boardRepository.delete(board);
    }

    private User findUser(String loginID) {
        return userRepository.findByLoginID(loginID)
                .orElseThrow(() -> new ResourceNotFoundException("유저 없음"));
    }

    private void requireActivePastor(User user) {
        if (user.getRole() != Role.PASTOR || !user.isActive()) {
            throw new ForbiddenActionException("목사님만 게시글을 작성할 수 있습니다.");
        }
    }

    private void requireOwnerOrActivePastor(Board board, String loginID) {
        User user = findUser(loginID);
        boolean isOwner = board.getUser().getLoginID().equals(loginID);
        boolean isActivePastor = user.getRole() == Role.PASTOR && user.isActive();

        if (!isOwner && !isActivePastor) {
            throw new ForbiddenActionException("권한이 없습니다.");
        }
    }
}
