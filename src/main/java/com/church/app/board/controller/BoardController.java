package com.church.app.board.controller;

import com.church.app.board.dto.BoardRequestDto;
import com.church.app.board.dto.BoardResponseDto;
import com.church.app.board.entity.Board;
import com.church.app.board.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardController {

    private final BoardService boardService;

    // 작성
    @PostMapping("/write")
    public String write(@RequestBody BoardRequestDto dto,
                        Authentication authentication) {

        String loginID = authentication.getName(); // 🔥 로그인 유저

        boardService.createBoard(dto, loginID);
        return "작성 완료";
    }

    // 최신순
    @GetMapping("/latest")
    public List<BoardResponseDto> latest() {
        return boardService.getAllBoardsDesc();
    }

    // 상태별 조회
    @GetMapping("/status/{status}")
    public List<BoardResponseDto> byStatus(@PathVariable String status) {

        Board.Status boardStatus =
                Board.Status.valueOf(status.toUpperCase());

        return boardService.getBoardsByStatus(boardStatus);
    }

    // 상세 조회 (읽음 처리)
    @GetMapping("/{id}")
    public BoardResponseDto detail(@PathVariable("id") Long id) {
        return boardService.getBoard(id);
    }

    // 수정
    @PutMapping("/{id}")
    public String update(@PathVariable("id") Long id,
                         @RequestBody BoardRequestDto dto,
                         Authentication authentication) {

        String loginID = authentication.getName();

        boardService.updateBoard(id, dto, loginID);
        return "수정 완료";
    }

    //  삭제
    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id") Long id,
                         Authentication authentication) {

        String loginID = authentication.getName();

        boardService.deleteBoard(id, loginID);
        return "삭제 완료";
    }
}