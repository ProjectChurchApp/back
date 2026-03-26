package com.church.app.board.service;

import com.church.app.board.dto.BoardRequestDto;
import com.church.app.board.dto.BoardResponseDto;
import com.church.app.board.entity.Board;
import com.church.app.board.repository.BoardRepository;
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
    private final UserRepository userRepository;

    public void createBoard(BoardRequestDto dto, String loginID) {
        User user = userRepository.findByLoginID(loginID)
                .orElseThrow(() -> new RuntimeException("유저 없음"));

        boardRepository.save(new Board(dto.getTitle(), dto.getContents(), user));
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
                .orElseThrow(() -> new RuntimeException("게시글 없음"));

        if (board.getStatus() == Board.Status.UNREAD) {
            board.markAsRead();
        }

        return new BoardResponseDto(board);
    }

    public void updateBoard(Long id, BoardRequestDto dto, String loginID) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("게시글 없음"));

        if (!board.getUser().getLoginID().equals(loginID)) {
            throw new RuntimeException("수정 권한 없음");
        }

        board.update(dto.getTitle(), dto.getContents());
    }

    public void deleteBoard(Long id, String loginID) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("게시글 없음"));

        if (!board.getUser().getLoginID().equals(loginID)) {
            throw new RuntimeException("삭제 권한 없음");
        }

        boardRepository.delete(board);
    }
}