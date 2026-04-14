package com.church.app.prayer.dto;

import com.church.app.prayer.entity.PrayerComment;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PrayerCommentResponseDto {

    private Long id;
    private String contents;
    private String writerName;
    private String writerLoginID;
    private LocalDateTime createdDate;

    public PrayerCommentResponseDto(PrayerComment comment) {
        this.id = comment.getId();
        this.contents = comment.getContents();
        this.writerName = comment.getUser().getName();
        this.writerLoginID = comment.getUser().getLoginID();
        this.createdDate = comment.getCreatedDate();
    }
}