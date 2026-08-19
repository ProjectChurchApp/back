package com.church.app.prayer.service;

import com.church.app.common.exception.ForbiddenActionException;
import com.church.app.common.exception.ResourceNotFoundException;
import com.church.app.connection.entity.PastorConnection;
import com.church.app.connection.repository.PastorConnectionRepository;
import com.church.app.notification.service.PushNotificationService;
import com.church.app.prayer.dto.PrayerCommentRequestDto;
import com.church.app.prayer.dto.PrayerCommentResponseDto;
import com.church.app.prayer.dto.PrayerPrayResponseDto;
import com.church.app.prayer.dto.PrayerRequestDto;
import com.church.app.prayer.dto.PrayerResponseDto;
import com.church.app.prayer.entity.Prayer;
import com.church.app.prayer.entity.PrayerComment;
import com.church.app.prayer.entity.PrayerPrayLog;
import com.church.app.prayer.repository.PrayerCommentRepository;
import com.church.app.prayer.repository.PrayerPrayLogRepository;
import com.church.app.prayer.repository.PrayerRepository;
import com.church.app.signup.entity.Role;
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
    private final PrayerPrayLogRepository prayerPrayLogRepository;
    private final PastorConnectionRepository connectionRepository;
    private final UserRepository userRepository;
    private final PushNotificationService pushNotificationService;

    // ── 기도 요청 작성 ────────────────────────────────────
    public void createPrayer(PrayerRequestDto dto, String loginID) {
        User user = findUser(loginID);

        Prayer.Visibility visibility = Prayer.Visibility.valueOf(dto.getVisibility());

        if (visibility == Prayer.Visibility.PASTOR) {
            User pastor = findConnectedPastor(user);

            Prayer prayer = new Prayer(user, dto.getTitle(), dto.getContents(), visibility);
            prayerRepository.save(prayer);

            pushNotificationService.sendToUser(
                    pastor.getLoginID(),
                    "새 기도 요청 🙏",
                    user.getName() + ": " + dto.getTitle()
            );
        } else {
            prayerRepository.save(new Prayer(user, dto.getTitle(), dto.getContents(), visibility));
        }
    }

    // ── 기도 요청 목록 조회 ───────────────────────────────
    public List<PrayerResponseDto> getPrayers(String loginID) {
        User user = findUser(loginID);

        List<Prayer> prayers;
        if (user.getRole() == Role.PASTOR && user.isActive()) {
            prayers = prayerRepository.findAllSharedWithPastor(
                    loginID, Prayer.BoardStage.SHARED_WITH_PASTOR, PastorConnection.Status.APPROVED);
        } else {
            prayers = prayerRepository.findAllByUserOrderByCreatedDateDesc(user);
        }

        return prayers.stream()
                .map(p -> new PrayerResponseDto(p, hasPrayed(p, user)))
                .toList();
    }

    // ── 중보기도 게시판 조회 ──────────────────────────────
    public List<PrayerResponseDto> getIntercessoryPrayers(String loginID) {
        User user = findUser(loginID);

        return prayerRepository.findAllByBoardStageOrderByCreatedDateDesc(Prayer.BoardStage.INTERCESSORY)
                .stream()
                .map(p -> new PrayerResponseDto(p, hasPrayed(p, user)))
                .toList();
    }

    // ── 기도 요청 상세 조회 ───────────────────────────────
    public PrayerResponseDto getPrayer(Long id, String loginID) {
        Prayer prayer = findPrayer(id);
        User user = findUser(loginID);

        requireCanView(prayer, user);

        return new PrayerResponseDto(prayer, hasPrayed(prayer, user));
    }

    // ── 기도 요청 수정 ────────────────────────────────────
    public void updatePrayer(Long id, PrayerRequestDto dto, String loginID) {
        Prayer prayer = findPrayer(id);
        User user = findUser(loginID);

        requireCanManage(prayer, user);

        Prayer.Visibility visibility = Prayer.Visibility.valueOf(dto.getVisibility());
        prayer.update(dto.getTitle(), dto.getContents(), visibility);
    }

    // ── 기도 상태 변경 (기도중/응답/종료) ──────────────────
    public void updateStatus(Long id, String status, String loginID) {
        Prayer prayer = findPrayer(id);
        User user = findUser(loginID);

        requireCanManage(prayer, user);

        prayer.changeStatus(Prayer.Status.valueOf(status));
    }

    // ── 중보기도로 승격 ───────────────────────────────────
    public void promote(Long id, String pastorLoginID) {
        Prayer prayer = findPrayer(id);
        User pastor = findUser(pastorLoginID);

        if (pastor.getRole() != Role.PASTOR || !pastor.isActive()) {
            throw new ForbiddenActionException("목사님만 중보기도로 공유할 수 있습니다.");
        }

        boolean isConnectedMember = connectionRepository.existsByMemberAndStatus(prayer.getUser(), PastorConnection.Status.APPROVED);
        if (!isConnectedMember) {
            throw new ForbiddenActionException("연결된 성도의 기도만 중보기도로 공유할 수 있습니다.");
        }

        if (prayer.getBoardStage() != Prayer.BoardStage.SHARED_WITH_PASTOR) {
            throw new IllegalArgumentException("목사님께 공유된 기도만 중보기도로 옮길 수 있습니다.");
        }

        prayer.promote(pastor);

        pushNotificationService.sendToAll(
                "중보기도 요청 🙏",
                prayer.getUser().getName() + "님의 기도가 중보기도로 공유되었습니다."
        );
    }

    // ── 기도했어요 토글 ───────────────────────────────────
    public PrayerPrayResponseDto togglePray(Long id, String loginID) {
        Prayer prayer = findPrayer(id);
        User user = findUser(loginID);

        requireCanView(prayer, user);

        var existing = prayerPrayLogRepository.findByPrayerAndUser(prayer, user);
        boolean hasPrayed;
        if (existing.isPresent()) {
            prayerPrayLogRepository.delete(existing.get());
            prayer.decrementPrayerCount();
            hasPrayed = false;
        } else {
            prayerPrayLogRepository.save(new PrayerPrayLog(prayer, user));
            prayer.incrementPrayerCount();
            hasPrayed = true;
        }

        return new PrayerPrayResponseDto(prayer.getPrayerCount(), hasPrayed);
    }

    // ── 기도 요청 삭제 ────────────────────────────────────
    public void deletePrayer(Long id, String loginID) {
        Prayer prayer = findPrayer(id);
        User user = findUser(loginID);

        requireCanManage(prayer, user);

        prayerRepository.delete(prayer);
    }

    // ── 댓글 목록 조회 ────────────────────────────────────
    public List<PrayerCommentResponseDto> getComments(Long prayerId, String loginID) {
        Prayer prayer = findPrayer(prayerId);
        User user = findUser(loginID);

        requireCanView(prayer, user);

        return prayerCommentRepository.findAllByPrayerIdOrderByCreatedDateAsc(prayerId)
                .stream()
                .map(PrayerCommentResponseDto::new)
                .toList();
    }

    // ── 댓글 작성 ─────────────────────────────────────────
    public void createComment(Long prayerId, PrayerCommentRequestDto dto, String loginID) {
        Prayer prayer = findPrayer(prayerId);
        User user = findUser(loginID);

        requireCanView(prayer, user);

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
                .orElseThrow(() -> new ResourceNotFoundException("댓글 없음"));

        if (!comment.getUser().getLoginID().equals(loginID)) {
            throw new ForbiddenActionException("수정 권한 없음");
        }

        comment.update(dto.getContents());
    }

    // ── 댓글 삭제 ─────────────────────────────────────────
    public void deleteComment(Long commentId, String loginID) {
        PrayerComment comment = prayerCommentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("댓글 없음"));

        User user = findUser(loginID);
        boolean isOwner = comment.getUser().getLoginID().equals(loginID);
        boolean isActivePastor = user.getRole() == Role.PASTOR && user.isActive();

        if (!isOwner && !isActivePastor) {
            throw new ForbiddenActionException("삭제 권한 없음");
        }

        prayerCommentRepository.delete(comment);
    }

    // ── 내부 헬퍼 ─────────────────────────────────────────
    private User findUser(String loginID) {
        return userRepository.findByLoginID(loginID)
                .orElseThrow(() -> new ResourceNotFoundException("유저 없음"));
    }

    private Prayer findPrayer(Long id) {
        return prayerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("기도 요청 없음"));
    }

    private User findConnectedPastor(User member) {
        return connectionRepository.findTopByMemberOrderByRequestedAtDesc(member)
                .filter(c -> c.getStatus() == PastorConnection.Status.APPROVED)
                .map(PastorConnection::getPastor)
                .orElseThrow(() -> new IllegalArgumentException("연결된 목사님이 없습니다. 먼저 목사님과 연결해주세요."));
    }

    private boolean isConnectedPastorOf(User pastor, User member) {
        return connectionRepository.findTopByMemberOrderByRequestedAtDesc(member)
                .filter(c -> c.getStatus() == PastorConnection.Status.APPROVED)
                .map(c -> c.getPastor().getLoginID().equals(pastor.getLoginID()))
                .orElse(false);
    }

    private void requireCanView(Prayer prayer, User user) {
        boolean isOwner = prayer.getUser().getLoginID().equals(user.getLoginID());
        boolean isIntercessory = prayer.getBoardStage() == Prayer.BoardStage.INTERCESSORY;
        boolean isConnectedPastor = user.getRole() == Role.PASTOR && user.isActive()
                && prayer.getBoardStage() == Prayer.BoardStage.SHARED_WITH_PASTOR
                && isConnectedPastorOf(user, prayer.getUser());

        if (!isOwner && !isIntercessory && !isConnectedPastor) {
            throw new ForbiddenActionException("접근 권한 없음");
        }
    }

    private void requireCanManage(Prayer prayer, User user) {
        if (prayer.isPromoted()) {
            boolean isActivePastor = user.getRole() == Role.PASTOR && user.isActive();
            if (!isActivePastor) {
                throw new ForbiddenActionException("중보기도로 공유된 기도는 목사님만 관리할 수 있습니다.");
            }
        } else {
            boolean isOwner = prayer.getUser().getLoginID().equals(user.getLoginID());
            if (!isOwner) {
                throw new ForbiddenActionException("본인의 기도만 관리할 수 있습니다.");
            }
        }
    }

    private boolean hasPrayed(Prayer prayer, User user) {
        return prayerPrayLogRepository.existsByPrayerAndUser(prayer, user);
    }
}
