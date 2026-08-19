package com.church.app.admin.service;

import com.church.app.admin.dto.PastorRequestDto;
import com.church.app.common.exception.ResourceNotFoundException;
import com.church.app.notification.service.PushNotificationService;
import com.church.app.signup.entity.AccountStatus;
import com.church.app.signup.entity.Role;
import com.church.app.signup.entity.User;
import com.church.app.signup.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminService {

    private final UserRepository userRepository;
    private final PushNotificationService pushNotificationService;

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
