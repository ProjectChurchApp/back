package com.church.app.connection.service;

import com.church.app.common.exception.ForbiddenActionException;
import com.church.app.common.exception.ResourceNotFoundException;
import com.church.app.connection.dto.ConnectionResponseDto;
import com.church.app.connection.dto.MemberSummaryDto;
import com.church.app.connection.dto.PastorOptionDto;
import com.church.app.connection.entity.PastorConnection;
import com.church.app.connection.repository.PastorConnectionRepository;
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
public class ConnectionService {

    private final PastorConnectionRepository connectionRepository;
    private final UserRepository userRepository;
    private final PushNotificationService pushNotificationService;

    public List<PastorOptionDto> listAvailablePastors() {
        return userRepository.findAllByRoleAndAccountStatus(Role.PASTOR, AccountStatus.ACTIVE)
                .stream()
                .map(PastorOptionDto::new)
                .toList();
    }

    public void requestConnection(String memberLoginID, String pastorLoginID) {
        User member = userRepository.findByLoginID(memberLoginID)
                .orElseThrow(() -> new ResourceNotFoundException("유저 없음"));
        User pastor = userRepository.findByLoginID(pastorLoginID)
                .orElseThrow(() -> new ResourceNotFoundException("목사님을 찾을 수 없습니다."));

        if (pastor.getRole() != Role.PASTOR || !pastor.isActive()) {
            throw new IllegalArgumentException("연결할 수 없는 목사님입니다.");
        }

        boolean hasActiveRequest = connectionRepository.existsByMemberAndStatus(member, PastorConnection.Status.PENDING)
                || connectionRepository.existsByMemberAndStatus(member, PastorConnection.Status.APPROVED);
        if (hasActiveRequest) {
            throw new IllegalArgumentException("이미 연결 요청 중이거나 연결된 목사님이 있습니다.");
        }

        connectionRepository.save(new PastorConnection(member, pastor));

        pushNotificationService.sendToUser(
                pastor.getLoginID(),
                "새 연결 요청 🔗",
                member.getName() + "님이 연결을 요청했습니다."
        );
    }

    public ConnectionResponseDto getMyConnection(String memberLoginID) {
        User member = userRepository.findByLoginID(memberLoginID)
                .orElseThrow(() -> new ResourceNotFoundException("유저 없음"));

        return connectionRepository.findTopByMemberOrderByRequestedAtDesc(member)
                .map(ConnectionResponseDto::new)
                .orElse(null);
    }

    public List<ConnectionResponseDto> getPendingForPastor(String pastorLoginID) {
        User pastor = userRepository.findByLoginID(pastorLoginID)
                .orElseThrow(() -> new ResourceNotFoundException("유저 없음"));

        return connectionRepository.findAllByPastorAndStatus(pastor, PastorConnection.Status.PENDING)
                .stream()
                .map(ConnectionResponseDto::new)
                .toList();
    }

    public void approve(Long connectionId, String pastorLoginID) {
        PastorConnection connection = connectionRepository.findById(connectionId)
                .orElseThrow(() -> new ResourceNotFoundException("연결 요청 없음"));

        if (!connection.getPastor().getLoginID().equals(pastorLoginID)) {
            throw new ForbiddenActionException("본인에게 온 요청만 승인할 수 있습니다.");
        }
        if (connection.getStatus() != PastorConnection.Status.PENDING) {
            throw new IllegalArgumentException("이미 처리된 요청입니다.");
        }

        connection.approve();

        pushNotificationService.sendToUser(
                connection.getMember().getLoginID(),
                "연결 승인 🙌",
                connection.getPastor().getName() + " 목사님과 연결되었습니다."
        );
    }

    public void reject(Long connectionId, String pastorLoginID) {
        PastorConnection connection = connectionRepository.findById(connectionId)
                .orElseThrow(() -> new ResourceNotFoundException("연결 요청 없음"));

        if (!connection.getPastor().getLoginID().equals(pastorLoginID)) {
            throw new ForbiddenActionException("본인에게 온 요청만 거절할 수 있습니다.");
        }
        if (connection.getStatus() != PastorConnection.Status.PENDING) {
            throw new IllegalArgumentException("이미 처리된 요청입니다.");
        }

        connection.reject();

        pushNotificationService.sendToUser(
                connection.getMember().getLoginID(),
                "연결 거절",
                connection.getPastor().getName() + " 목사님이 연결 요청을 거절했습니다."
        );
    }

    public List<MemberSummaryDto> getMyMembers(String pastorLoginID) {
        User pastor = userRepository.findByLoginID(pastorLoginID)
                .orElseThrow(() -> new ResourceNotFoundException("유저 없음"));

        return connectionRepository.findAllByPastorAndStatus(pastor, PastorConnection.Status.APPROVED)
                .stream()
                .map(c -> new MemberSummaryDto(c.getMember()))
                .toList();
    }
}
