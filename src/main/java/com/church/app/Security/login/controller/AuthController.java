package com.church.app.Security.login.controller;

import com.church.app.Security.login.dto.ApiResponse;
import com.church.app.Security.login.dto.UserResponse;
import com.church.app.signup.entity.User;
import com.church.app.signup.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserRepository userRepository;
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(401)
                    .body(new ApiResponse(false, "인증되지 않았습니다."));
        }

        String loginID = auth.getName();
        User user = userRepository.findByLoginID(loginID)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));



        return ResponseEntity.ok(new UserResponse(
                user.getLoginID(),
                user.getName(),
                user.getRole()
        ));
    }
}
