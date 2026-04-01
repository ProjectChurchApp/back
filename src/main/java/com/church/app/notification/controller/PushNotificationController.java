package com.church.app.notification.controller;

import com.church.app.notification.service.PushNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notification")
public class PushNotificationController {

    private final PushNotificationService pushNotificationService;

    // 푸시 토큰 저장
    @PostMapping("/token")
    public String saveToken(@RequestBody Map<String, String> body,
                            Authentication authentication) {
        pushNotificationService.saveToken(
                authentication.getName(),
                body.get("token")
        );
        return "토큰 저장 완료";
    }

    // 푸시 토큰 삭제 (로그아웃 시)
    @DeleteMapping("/token")
    public String deleteToken(@RequestBody Map<String, String> body) {
        pushNotificationService.deleteToken(body.get("token"));
        return "토큰 삭제 완료";
    }
}
