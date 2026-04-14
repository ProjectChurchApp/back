package com.church.app.prayer.dto;

import com.church.app.prayer.entity.Prayer;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PrayerResponseDto {

    private Long id;
    private String title;
    private String contents;
    private String writerName;
    private String writerLoginID;
    private String visibility;
    private LocalDateTime createdDate;

    public PrayerResponseDto(Prayer prayer) {
        this.id = prayer.getId();
        this.title = prayer.getTitle();
        this.contents = prayer.getContents();
        this.writerName = prayer.getUser().getName();
        this.writerLoginID = prayer.getUser().getLoginID();
        this.visibility = prayer.getVisibility().name();
        this.createdDate = prayer.getCreatedDate();
    }
}