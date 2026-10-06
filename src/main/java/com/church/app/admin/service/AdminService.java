package com.church.app.admin.service;

import com.church.app.admin.dto.PastorRequestDto;
import com.church.app.admin.dto.UserSummaryDto;
import com.church.app.common.exception.ResourceNotFoundException;
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

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminService {

    private final UserRepository userRepository;
    private final PushNotificationService pushNotificationService;
    private final PasswordEncoder passwordEncoder;

    // 임시 비밀번호에 쓰는 글자. 전화로 불러줄 수 있도록 O/0, I/l 처럼 헷갈리는 글자는 뺐다.
    private static final String PW_LETTERS = "abcdefghjkmnpqrstuvwxyzABCDEFGHJKMNPQRSTUVWXYZ";
    private static final String PW_DIGITS = "23456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    /** 전체 가입자 목록. 비밀번호는 복원할 수 없으므로 포함하지 않는다. */
    public List<UserSummaryDto> getAllUsers() {
        return userRepository.findAll().stream()
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

    /** 계정을 정지시킨다. 작성한 글과 댓글은 남는다. */
    public void suspendUser(Integer userId, String adminLoginID) {
        User admin = findAdmin(adminLoginID);
        User target = findUserById(userId);

        if (target.getUserId() == admin.getUserId()) {
            throw new IllegalArgumentException("본인 계정은 정지할 수 없습니다.");
        }
        if (target.getRole() == Role.ADMIN) {
            throw new IllegalArgumentException("관리자 계정은 정지할 수 없습니다.");
        }

        target.suspend();
    }

    /** 정지를 해제한다. */
    public void activateUser(Integer userId, String adminLoginID) {
        findAdmin(adminLoginID);
        findUserById(userId).reactivate();
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
