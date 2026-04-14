package com.church.app.prayer.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PrayerRequestDto {
    private String title;
    private String contents;
    private String visibility; // PRIVATE or PASTOR
}