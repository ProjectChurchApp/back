package com.church.app.admin.controller;

import com.church.app.admin.dto.PastorRequestDto;
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
}
