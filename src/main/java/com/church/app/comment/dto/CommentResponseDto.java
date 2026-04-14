package com.church.app.comment.dto;

import com.church.app.comment.entity.Comment;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class CommentResponseDto {

    private Long id;
    private String contents;
    private String writerName;
    private String writerLoginID;
    private LocalDateTime createdDate;

    public CommentResponseDto(Comment comment) {
        this.id = comment.getId();
        this.contents = comment.getContents();
        this.writerName = comment.getUser().getName();
        this.writerLoginID = comment.getUser().getLoginID();
        this.createdDate = comment.getCreatedDate();
    }
}