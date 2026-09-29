package com.church.app.notification.service;

import com.church.app.notification.entity.PushToken;
import com.church.app.notification.repository.PushTokenRepository;
import com.church.app.signup.entity.User;
import com.church.app.signup.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class PushNotificationService {

    private final PushTokenRepository pushTokenRepository;
    private final UserRepository userRepository;

    // 토큰 저장
    public void saveToken(String loginID, String token) {
        User user = userRepository.findByLoginID(loginID)
                .orElseThrow(() -> new RuntimeException("유저 없음"));

        // 이미 있으면 저장 안 함
        if (pushTokenRepository.findByToken(token).isEmpty()) {
            pushTokenRepository.save(new PushToken(user, token));
        }
    }

    // 토큰 삭제 (로그아웃 시)
    public void deleteToken(String token) {
        pushTokenRepository.deleteByToken(token);
    }

    // 전체 유저에게 푸시 전송
    public void sendToAll(String title, String body) {
        send(pushTokenRepository.findAll(), title, body);
    }

    // 특정 유저에게 푸시 전송
    public void sendToUser(String loginID, String title, String body) {
        User user = userRepository.findByLoginID(loginID).orElse(null);
        if (user == null) return;

        send(pushTokenRepository.findAllByUser(user), title, body);
    }

    // 여러 유저에게 한 번에 푸시 전송 (중보기도 그룹 등)
    public void sendToUsers(List<String> loginIDs, String title, String body) {
        if (loginIDs == null || loginIDs.isEmpty()) return;

        List<PushToken> tokens = new ArrayList<>();
        for (String loginID : loginIDs) {
            userRepository.findByLoginID(loginID)
                    .ifPresent(user -> tokens.addAll(pushTokenRepository.findAllByUser(user)));
        }

        send(tokens, title, body);
    }

    private void send(List<PushToken> tokens, String title, String body) {
        if (tokens.isEmpty()) return;

        List<Map<String, Object>> messages = tokens.stream()
                .map(pt -> {
                    Map<String, Object> msg = new HashMap<>();
                    msg.put("to", pt.getToken());
                    msg.put("title", title);
                    msg.put("body", body);
                    msg.put("sound", "default");
                    return msg;
                })
                .toList();

        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<List<Map<String, Object>>> request = new HttpEntity<>(messages, headers);

            restTemplate.postForObject(
                    "https://exp.host/--/api/v2/push/send",
                    request,
                    String.class
            );
        } catch (Exception e) {
            // 푸시 실패해도 본 작업은 정상 처리
            System.err.println("푸시 전송 실패: " + e.getMessage());
        }
    }
}
