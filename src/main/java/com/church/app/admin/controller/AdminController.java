package com.church.app.admin.controller;

import com.church.app.admin.dto.PastorRequestDto;
import com.church.app.admin.dto.UserSummaryDto;

import java.util.Map;
import com.church.app.admin.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/pastor-requests")
    public List<PastorRequestDto> pastorRequests() {
        return adminService.getPendingPastorRequests();
    }

    @PostMapping("/pastor-requests/{userId}/approve")
    public String approve(@PathVariable Integer userId, Authentication authentication) {
        adminService.approvePastor(userId, authentication.getName());
        return "승인 완료";
    }

    @PostMapping("/pastor-requests/{userId}/reject")
    public String reject(@PathVariable Integer userId, Authentication authentication) {
        adminService.rejectPastor(userId, authentication.getName());
        return "거절 완료";
    }

    // ── 가입자 관리 ──────────────────────────────────────

    @GetMapping("/users")
    public List<UserSummaryDto> users() {
        return adminService.getAllUsers();
    }

    /** 임시 비밀번호를 발급해 한 번만 돌려준다. */
    @PostMapping("/users/{userId}/reset-password")
    public Map<String, String> resetPassword(@PathVariable Integer userId, Authentication authentication) {
        return Map.of("temporaryPassword", adminService.resetPassword(userId, authentication.getName()));
    }

    /** 탈퇴 처리 — 아이디를 비워 재가입할 수 있게 한다. 글과 댓글은 남는다. */
    @DeleteMapping("/users/{userId}")
    public String withdraw(@PathVariable Integer userId, Authentication authentication) {
        adminService.withdrawUser(userId, authentication.getName());
        return "탈퇴 처리 완료";
    }

}
