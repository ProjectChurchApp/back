package com.church.app.prayer.controller;

import com.church.app.prayer.dto.PrayerCommentRequestDto;
import com.church.app.prayer.dto.PrayerCommentResponseDto;
import com.church.app.prayer.dto.PrayerRequestDto;
import com.church.app.prayer.dto.PrayerResponseDto;
import com.church.app.prayer.service.PrayerService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/prayer")
public class PrayerController {

    private final PrayerService prayerService;

    // 목록 조회
    @GetMapping
    public List<PrayerResponseDto> list(Authentication authentication) {
        return prayerService.getPrayers(authentication.getName());
    }

    // 상세 조회
    @GetMapping("/{id}")
    public PrayerResponseDto detail(@PathVariable Long id, Authentication authentication) {
        return prayerService.getPrayer(id, authentication.getName());
    }

    // 작성
    @PostMapping
    public String create(@RequestBody PrayerRequestDto dto, Authentication authentication) {
        prayerService.createPrayer(dto, authentication.getName());
        return "작성 완료";
    }

    // 수정
    @PutMapping("/{id}")
    public String update(@PathVariable Long id,
                         @RequestBody PrayerRequestDto dto,
                         Authentication authentication) {
        prayerService.updatePrayer(id, dto, authentication.getName());
        return "수정 완료";
    }

    // 삭제
    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id, Authentication authentication) {
        prayerService.deletePrayer(id, authentication.getName());
        return "삭제 완료";
    }

    // 댓글 목록
    @GetMapping("/{id}/comments")
    public List<PrayerCommentResponseDto> getComments(@PathVariable Long id) {
        return prayerService.getComments(id);
    }

    // 댓글 작성
    @PostMapping("/{id}/comments")
    public String createComment(@PathVariable Long id,
                                @RequestBody PrayerCommentRequestDto dto,
                                Authentication authentication) {
        prayerService.createComment(id, dto, authentication.getName());
        return "댓글 작성 완료";
    }

    // 댓글 수정
    @PutMapping("/comments/{commentId}")
    public String updateComment(@PathVariable Long commentId,
                                @RequestBody PrayerCommentRequestDto dto,
                                Authentication authentication) {
        prayerService.updateComment(commentId, dto, authentication.getName());
        return "수정 완료";
    }

    // 댓글 삭제
    @DeleteMapping("/comments/{commentId}")
    public String deleteComment(@PathVariable Long commentId, Authentication authentication) {
        prayerService.deleteComment(commentId, authentication.getName());
        return "삭제 완료";
    }
}