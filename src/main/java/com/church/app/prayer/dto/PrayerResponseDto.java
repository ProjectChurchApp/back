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
    private String boardStage;
    private String status;
    private int prayerCount;
    private boolean hasPrayed;
    private String promotedByName;
    private LocalDateTime promotedAt;
    private LocalDateTime createdDate;

    public PrayerResponseDto(Prayer prayer, boolean hasPrayed) {
        this.id = prayer.getId();
        this.title = prayer.getTitle();
        this.contents = prayer.getContents();
        this.writerName = prayer.getUser().getName();
        this.writerLoginID = prayer.getUser().getLoginID();
        this.visibility = prayer.getVisibility().name();
        this.boardStage = prayer.getBoardStage().name();
        this.status = prayer.getStatus().name();
        this.prayerCount = prayer.getPrayerCount();
        this.hasPrayed = hasPrayed;
        this.promotedByName = prayer.getPromotedBy() != null ? prayer.getPromotedBy().getName() : null;
        this.promotedAt = prayer.getPromotedAt();
        this.createdDate = prayer.getCreatedDate();
    }
}
