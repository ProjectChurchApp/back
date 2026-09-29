package com.church.app.prayer.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PrayerRequestDto {
    private String title;
    private String contents;
    private String visibility; // PRIVATE or PASTOR
    private boolean intercessoryRequested; // visibility=PASTOR일 때 중보기도까지 공유 요청 여부
}
