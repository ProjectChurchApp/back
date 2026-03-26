package com.church.app.board.dto;

import com.church.app.board.entity.Board;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class BoardResponseDto {

    private Long id;
    private String title;
    private String contents;
    private String writerName;
    private String status;
    private LocalDateTime createdDate;

    public BoardResponseDto(Board board) {
        this.id = board.getId();
        this.title = board.getTitle();
        this.contents = board.getContents();
        this.writerName = board.getUser().getName();
        this.status = board.getStatus().name();
        this.createdDate = board.getCreatedDate();
    }
}