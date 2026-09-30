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

        requireOwner(board, loginID);

        board.update(dto.getTitle(), dto.getContents());
    }

    public void deleteBoard(Long id, String loginID) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("게시글 없음"));

        requireOwner(board, loginID);

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

    /**
     * 게시글 수정·삭제는 작성자 본인만 할 수 있다.
     *
     * 요구사항 문서가 목사님께 부여한 것은 "작성 권한"과 "성도 댓글 삭제"뿐이고,
     * 다른 목사님의 공지를 지울 수 있다는 내용은 없다. 오히려 목사 권한 아이디가
     * 여럿이라 관리체계가 모호하다는 점을 문제로 적어두었으므로 작성자로 좁힌다.
     */
    private void requireOwner(Board board, String loginID) {
        if (!board.getUser().getLoginID().equals(loginID)) {
            throw new ForbiddenActionException("본인이 쓴 글만 수정하거나 삭제할 수 있습니다.");
        }
    }
}
