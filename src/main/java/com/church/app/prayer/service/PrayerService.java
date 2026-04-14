package com.church.app.prayer.service;

import com.church.app.notification.service.PushNotificationService;
import com.church.app.prayer.dto.PrayerCommentRequestDto;
import com.church.app.prayer.dto.PrayerCommentResponseDto;
import com.church.app.prayer.dto.PrayerRequestDto;
import com.church.app.prayer.dto.PrayerResponseDto;
import com.church.app.prayer.entity.Prayer;
import com.church.app.prayer.entity.PrayerComment;
import com.church.app.prayer.repository.PrayerCommentRepository;
import com.church.app.prayer.repository.PrayerRepository;
import com.church.app.signup.entity.User;
import com.church.app.signup.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class PrayerService {

    private final PrayerRepository prayerRepository;
    private final PrayerCommentRepository prayerCommentRepository;
    private final UserRepository userRepository;
    private final PushNotificationService pushNotificationService;

    // ── 기도 요청 작성 ────────────────────────────────────
    public void createPrayer(PrayerRequestDto dto, String loginID) {
        User user = userRepository.findByLoginID(loginID)
                .orElseThrow(() -> new RuntimeException("유저 없음"));

        Prayer.Visibility visibility = Prayer.Visibility.valueOf(dto.getVisibility());
        prayerRepository.save(new Prayer(user, dto.getTitle(), dto.getContents(), visibility));

        // PASTOR 선택 시 목사에게 알림
        if (visibility == Prayer.Visibility.PASTOR) {
            userRepository.findAll().stream()
                    .filter(u -> u.getRole().equals("목사"))
                    .forEach(pastor -> pushNotificationService.sendToUser(
                            pastor.getLoginID(),
                            "새 기도 요청 🙏",
                            user.getName() + ": " + dto.getTitle()
                    ));
        }
    }

    // ── 기도 요청 목록 조회 ───────────────────────────────
    public List<PrayerResponseDto> getPrayers(String loginID) {
        User user = userRepository.findByLoginID(loginID)
                .orElseThrow(() -> new RuntimeException("유저 없음"));

        // 목사면 PASTOR 공개글 + 본인 글 전체
        // 신도면 본인 글만
        if (user.getRole().equals("목사")) {
            return prayerRepository.findAllVisibleToUser(user)
                    .stream()
                    .map(PrayerResponseDto::new)
                    .toList();
        } else {
            return prayerRepository.findAllByUserOrderByCreatedDateDesc(user)
                    .stream()
                    .map(PrayerResponseDto::new)
                    .toList();
        }
    }

    // ── 기도 요청 상세 조회 ───────────────────────────────
    public PrayerResponseDto getPrayer(Long id, String loginID) {
        Prayer prayer = prayerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("기도 요청 없음"));

        User user = userRepository.findByLoginID(loginID)
                .orElseThrow(() -> new RuntimeException("유저 없음"));

        // 권한 체크
        boolean isOwner = prayer.getUser().getLoginID().equals(loginID);
        boolean isPastor = user.getRole().equals("목사");
        boolean isPastorVisible = prayer.getVisibility() == Prayer.Visibility.PASTOR;

        if (!isOwner && !(isPastor && isPastorVisible)) {
            throw new RuntimeException("접근 권한 없음");
        }

        return new PrayerResponseDto(prayer);
    }

    // ── 기도 요청 수정 ────────────────────────────────────
    public void updatePrayer(Long id, PrayerRequestDto dto, String loginID) {
        Prayer prayer = prayerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("기도 요청 없음"));

        if (!prayer.getUser().getLoginID().equals(loginID)) {
            throw new RuntimeException("수정 권한 없음");
        }

        Prayer.Visibility visibility = Prayer.Visibility.valueOf(dto.getVisibility());
        prayer.update(dto.getTitle(), dto.getContents(), visibility);
    }

    // ── 기도 요청 삭제 ────────────────────────────────────
    public void deletePrayer(Long id, String loginID) {
        Prayer prayer = prayerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("기도 요청 없음"));

        if (!prayer.getUser().getLoginID().equals(loginID)) {
            throw new RuntimeException("삭제 권한 없음");
        }

        prayerRepository.delete(prayer);
    }

    // ── 댓글 목록 조회 ────────────────────────────────────
    public List<PrayerCommentResponseDto> getComments(Long prayerId) {
        return prayerCommentRepository.findAllByPrayerIdOrderByCreatedDateAsc(prayerId)
                .stream()
                .map(PrayerCommentResponseDto::new)
                .toList();
    }

    // ── 댓글 작성 ─────────────────────────────────────────
    public void createComment(Long prayerId, PrayerCommentRequestDto dto, String loginID) {
        Prayer prayer = prayerRepository.findById(prayerId)
                .orElseThrow(() -> new RuntimeException("기도 요청 없음"));

        User user = userRepository.findByLoginID(loginID)
                .orElseThrow(() -> new RuntimeException("유저 없음"));

        prayerCommentRepository.save(new PrayerComment(prayer, user, dto.getContents()));

        // 게시글 작성자 + 기존 댓글 작성자 모두에게 알림 (본인 제외)
        Set<String> notifyTargets = new HashSet<>();
        notifyTargets.add(prayer.getUser().getLoginID());
        prayerCommentRepository.findAllByPrayerIdOrderByCreatedDateAsc(prayerId)
                .forEach(c -> notifyTargets.add(c.getUser().getLoginID()));
        notifyTargets.remove(loginID);

        notifyTargets.forEach(id -> pushNotificationService.sendToUser(
                id,
                "새 댓글 💬",
                user.getName() + ": " + dto.getContents()
        ));
    }

    // ── 댓글 수정 ─────────────────────────────────────────
    public void updateComment(Long commentId, PrayerCommentRequestDto dto, String loginID) {
        PrayerComment comment = prayerCommentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("댓글 없음"));

        if (!comment.getUser().getLoginID().equals(loginID)) {
            throw new RuntimeException("수정 권한 없음");
        }

        comment.update(dto.getContents());
    }

    // ── 댓글 삭제 ─────────────────────────────────────────
    public void deleteComment(Long commentId, String loginID) {
        PrayerComment comment = prayerCommentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("댓글 없음"));

        if (!comment.getUser().getLoginID().equals(loginID)) {
            throw new RuntimeException("삭제 권한 없음");
        }

        prayerCommentRepository.delete(comment);
    }
}