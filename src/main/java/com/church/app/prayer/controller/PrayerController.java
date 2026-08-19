package com.church.app.prayer.controller;

import com.church.app.prayer.dto.PrayerCommentRequestDto;
import com.church.app.prayer.dto.PrayerCommentResponseDto;
import com.church.app.prayer.dto.PrayerPrayResponseDto;
import com.church.app.prayer.dto.PrayerRequestDto;
import com.church.app.prayer.dto.PrayerResponseDto;
import com.church.app.prayer.dto.PrayerStatusUpdateDto;
import com.church.app.prayer.service.PrayerService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/prayer")
public class PrayerController {

    private final PrayerService prayerService;

    // 목록 조회 (나의기도: 본인 전체 / 목사: 연결된 성도의 공유분)
    @GetMapping
    public List<PrayerResponseDto> list(Authentication authentication) {
        return prayerService.getPrayers(authentication.getName());
    }

    // 중보기도 게시판 (전체 열람)
    @GetMapping("/intercessory")
    public List<PrayerResponseDto> intercessory(Authentication authentication) {
        return prayerService.getIntercessoryPrayers(authentication.getName());
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

    // 상태 변경 (기도중/응답/종료)
    @PutMapping("/{id}/status")
    public String updateStatus(@PathVariable Long id,
                                @RequestBody PrayerStatusUpdateDto dto,
                                Authentication authentication) {
        prayerService.updateStatus(id, dto.status(), authentication.getName());
        return "상태 변경 완료";
    }

    // 중보기도로 공유 (목사 전용)
    @PostMapping("/{id}/promote")
    @PreAuthorize("hasAuthority('ROLE_PASTOR')")
    public String promote(@PathVariable Long id, Authentication authentication) {
        prayerService.promote(id, authentication.getName());
        return "중보기도로 공유 완료";
    }

    // 기도했어요 토글
    @PostMapping("/{id}/pray")
    public PrayerPrayResponseDto pray(@PathVariable Long id, Authentication authentication) {
        return prayerService.togglePray(id, authentication.getName());
    }

    // 삭제
    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id, Authentication authentication) {
        prayerService.deletePrayer(id, authentication.getName());
        return "삭제 완료";
    }

    // 댓글 목록
    @GetMapping("/{id}/comments")
    public List<PrayerCommentResponseDto> getComments(@PathVariable Long id, Authentication authentication) {
        return prayerService.getComments(id, authentication.getName());
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
