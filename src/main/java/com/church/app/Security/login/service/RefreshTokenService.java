package com.church.app.Security.login.service;

import com.church.app.Security.login.dto.RefreshResult;
import com.church.app.Security.login.entity.RefreshToken;
import com.church.app.Security.login.repository.RefreshTokenRepository;
import com.church.app.Security.login.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtils jwtUtils;

    @Transactional
    public String createRefreshToken(String loginID) {

        // 예전에는 여기서 그 계정의 토큰을 전부 지웠다. 그러면 다른 기기에서 로그인하는
        // 순간 먼저 쓰던 기기의 세션이 조용히 끊겨, 그 기기는 재발급에 실패하고
        // 로그아웃된다. 만료된 것만 정리해 기기별 로그인이 함께 유지되도록 한다.
        List<RefreshToken> expired = refreshTokenRepository.findByLoginID(loginID).stream()
                .filter(RefreshToken::isExpired)
                .toList();
        if (!expired.isEmpty()) {
            refreshTokenRepository.deleteAll(expired);
        }

        // 새로운 리프레쉬 토큰 생성 (UUID)
        String token = jwtUtils.generateRefreshToken();

        // 만료 시간 계산 (14일)
        LocalDateTime expiryDate = LocalDateTime.now()
                .plusSeconds(jwtUtils.getRefreshTokenExpirationInMillis() / 1000);

        RefreshToken refreshToken = new RefreshToken(token, loginID, expiryDate);
        refreshTokenRepository.save(refreshToken);

        return token;
    }

    //RefreshToken 검증 로직
    @Transactional
    public RefreshResult refreshAccessToken(String refreshTokenValue) {

        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenValue)
                .orElseThrow(() -> {
                    //
                    return new RuntimeException("리프레쉬 토큰이 없습니다");
                });

        String loginID = refreshToken.getLoginID();

        // 리프레쉬 토큰 탈취 감지
        if (refreshToken.isUsed()) {

            revokeAllUserTokens(loginID);

            throw new RuntimeException("토큰 재사용이 감지되었습니다 모든 세션을 취소합니다");
        }

        // 토큰 취소 확인
        if (refreshToken.isRevoked()) {
            throw new RuntimeException("취소된 토큰");
        }

        // 토큰 만료 확인
        if (refreshToken.isExpired()) {
            throw new RuntimeException("만료된 리프레쉬 토큰");
        }

        //현재 토큰을 "사용됨"으로 표시(Rotation)
        refreshToken.markAsUsed();
        refreshTokenRepository.save(refreshToken);


        //  새로운 Refresh Token 생성 (Rotation)
        String newRefreshToken = createRefreshToken(loginID);


        // loginID와 새로운 Refresh Token 반환
        return new RefreshResult(newRefreshToken, loginID);
    }

    //모든 Token 삭제
    @Transactional
    public void revokeAllUserTokens(String loginID) {
        List<RefreshToken> tokens = refreshTokenRepository.findByLoginID(loginID);
        tokens.forEach(RefreshToken::revoke);
        refreshTokenRepository.saveAll(tokens);
    }

    // 로그아웃시 리프레쉬 토큰 삭제
    @Transactional
    public void deleteRefreshToken(String loginID) {
        refreshTokenRepository.deleteByLoginID(loginID);
    }
    // 리프레쉬 토큰 유효성 검사
    public boolean validateRefreshToken(String token) {
        return refreshTokenRepository.findByToken(token)
                .map(RefreshToken::isValid)
                .orElse(false);
    }
    // 리프레쉬 토큰에서 사용자 추출
    public String getLoginIDFromToken(String token) {
        return refreshTokenRepository.findByToken(token)
                .map(RefreshToken::getLoginID)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));
    }
}
