package com.church.app.admin.service;

import com.church.app.admin.dto.PastorRequestDto;
import com.church.app.admin.dto.UserSummaryDto;
import com.church.app.common.exception.ResourceNotFoundException;
import com.church.app.Security.login.repository.RefreshTokenRepository;
import com.church.app.notification.repository.PushTokenRepository;
import com.church.app.notification.service.PushNotificationService;
import com.church.app.signup.entity.AccountStatus;
import com.church.app.signup.entity.Role;
import com.church.app.signup.entity.User;
import com.church.app.signup.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.UUID;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminService {

    private final UserRepository userRepository;
    private final PushNotificationService pushNotificationService;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PushTokenRepository pushTokenRepository;

    // 임시 비밀번호에 쓰는 글자. 전화로 불러줄 수 있도록 O/0, I/l 처럼 헷갈리는 글자는 뺐다.
    private static final String PW_LETTERS = "abcdefghjkmnpqrstuvwxyzABCDEFGHJKMNPQRSTUVWXYZ";
    private static final String PW_DIGITS = "23456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * 가입자 목록. 비밀번호는 복원할 수 없으므로 포함하지 않는다.
     *
     * 탈퇴한 계정은 제외한다. 관리자가 할 수 있는 일이 없고, 사용자 입장에서는
     * 이미 지워진 계정이다. DB 행을 남겨두는 것은 그 사람이 쓴 글과 댓글의
     * 작성자 표시가 깨지지 않게 하기 위한 내부 사정일 뿐이다.
     */
    public List<UserSummaryDto> getAllUsers() {
        return userRepository.findAll().stream()
                .filter(u -> u.getAccountStatus() != AccountStatus.WITHDRAWN)
                .map(UserSummaryDto::new)
                .toList();
    }

    /**
     * 임시 비밀번호를 발급한다. 돌려준 값은 화면에 한 번 보여주고 버린다.
     * 저장되는 것은 해시뿐이라 이후에는 관리자도 다시 확인할 수 없다.
     */
    public String resetPassword(Integer userId, String adminLoginID) {
        findAdmin(adminLoginID);
        User target = findUserById(userId);

        String temporary = generateTemporaryPassword();
        target.changePassword(passwordEncoder.encode(temporary));

        pushNotificationService.sendToUser(
                target.getLoginID(),
                "비밀번호가 초기화되었습니다",
                "관리자가 임시 비밀번호를 발급했습니다. 로그인 후 바꿔주세요."
        );

        return temporary;
    }

    /**
     * 탈퇴 처리. 쓰던 아이디를 비워 같은 아이디로 재가입할 수 있게 한다.
     * 글과 댓글은 "탈퇴한 사용자" 이름으로 남는다.
     */
    public void withdrawUser(Integer userId, String adminLoginID) {
        User admin = findAdmin(adminLoginID);
        User target = findUserById(userId);

        if (target.getUserId() == admin.getUserId()) {
            throw new IllegalArgumentException("본인 계정은 탈퇴 처리할 수 없습니다.");
        }
        if (target.getRole() == Role.ADMIN) {
            throw new IllegalArgumentException("관리자 계정은 탈퇴 처리할 수 없습니다.");
        }
        if (target.getAccountStatus() == AccountStatus.WITHDRAWN) {
            throw new IllegalArgumentException("이미 탈퇴 처리된 계정입니다.");
        }

        // 로그인 아이디가 바뀌면 기존 토큰을 찾을 수 없으므로 세션부터 끊는다.
        refreshTokenRepository.deleteByLoginID(target.getLoginID());
        pushTokenRepository.deleteAll(pushTokenRepository.findAllByUser(target));

        target.withdraw("deleted_" + target.getUserId(),
                passwordEncoder.encode(UUID.randomUUID().toString()));
    }

    private User findUserById(Integer userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("해당 사용자가 없습니다."));
    }

    private String generateTemporaryPassword() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 4; i++) sb.append(PW_LETTERS.charAt(RANDOM.nextInt(PW_LETTERS.length())));
        for (int i = 0; i < 4; i++) sb.append(PW_DIGITS.charAt(RANDOM.nextInt(PW_DIGITS.length())));
        sb.append('!');
        return sb.toString();
    }

    public List<PastorRequestDto> getPendingPastorRequests() {
        return userRepository.findAllByRoleAndAccountStatus(Role.PASTOR, AccountStatus.PENDING_PASTOR_APPROVAL)
                .stream()
                .map(PastorRequestDto::new)
                .toList();
    }

    public void approvePastor(Integer userId, String adminLoginID) {
        User admin = findAdmin(adminLoginID);
        User target = findPastorRequest(userId);

        target.approve(admin);

        pushNotificationService.sendToUser(
                target.getLoginID(),
                "목사 승인 완료 ✅",
                "관리자가 목사 계정을 승인했습니다."
        );
    }

    public void rejectPastor(Integer userId, String adminLoginID) {
        User admin = findAdmin(adminLoginID);
        User target = findPastorRequest(userId);

        target.reject(admin);

        pushNotificationService.sendToUser(
                target.getLoginID(),
                "목사 승인 거절",
                "관리자가 목사 승인 요청을 거절했습니다."
        );
    }

    private User findAdmin(String adminLoginID) {
        return userRepository.findByLoginID(adminLoginID)
                .orElseThrow(() -> new ResourceNotFoundException("유저 없음"));
    }

    private User findPastorRequest(Integer userId) {
        User target = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("대상 사용자를 찾을 수 없습니다."));

        if (target.getRole() != Role.PASTOR) {
            throw new IllegalArgumentException("목사 가입 신청이 아닙니다.");
        }

        return target;
    }
}
