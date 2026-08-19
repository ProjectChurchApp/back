package com.church.app.connection.controller;

import com.church.app.connection.dto.ConnectionRequestDto;
import com.church.app.connection.dto.ConnectionResponseDto;
import com.church.app.connection.dto.MemberSummaryDto;
import com.church.app.connection.dto.PastorOptionDto;
import com.church.app.connection.service.ConnectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/connections")
public class ConnectionController {

    private final ConnectionService connectionService;

    @GetMapping("/pastors")
    public List<PastorOptionDto> pastors() {
        return connectionService.listAvailablePastors();
    }

    @PostMapping
    public String requestConnection(@RequestBody ConnectionRequestDto dto, Authentication authentication) {
        connectionService.requestConnection(authentication.getName(), dto.pastorLoginID());
        return "연결 요청 완료";
    }

    @GetMapping("/me")
    public ConnectionResponseDto me(Authentication authentication) {
        return connectionService.getMyConnection(authentication.getName());
    }

    @GetMapping("/pending")
    @PreAuthorize("hasAuthority('ROLE_PASTOR')")
    public List<ConnectionResponseDto> pending(Authentication authentication) {
        return connectionService.getPendingForPastor(authentication.getName());
    }

    @PatchMapping("/{id}/approve")
    @PreAuthorize("hasAuthority('ROLE_PASTOR')")
    public String approve(@PathVariable Long id, Authentication authentication) {
        connectionService.approve(id, authentication.getName());
        return "승인 완료";
    }

    @PatchMapping("/{id}/reject")
    @PreAuthorize("hasAuthority('ROLE_PASTOR')")
    public String reject(@PathVariable Long id, Authentication authentication) {
        connectionService.reject(id, authentication.getName());
        return "거절 완료";
    }

    @GetMapping("/my-members")
    @PreAuthorize("hasAuthority('ROLE_PASTOR')")
    public List<MemberSummaryDto> myMembers(Authentication authentication) {
        return connectionService.getMyMembers(authentication.getName());
    }
}
