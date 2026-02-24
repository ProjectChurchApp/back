package com.church.app.Security.login.controller;

import com.church.app.Security.login.dto.ApiResponse;
import com.church.app.Security.login.dto.ErrorResponse;
import com.church.app.Security.login.dto.RefreshResult;
import com.church.app.Security.login.dto.TokenResponse;
import com.church.app.Security.login.service.RefreshTokenService;
import com.church.app.Security.login.service.UserDetailServiceImpl;
import com.church.app.Security.login.utils.JwtUtils;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class LoginController {
    private final RefreshTokenService refreshTokenService;
    private final JwtUtils jwtUtils;
    private final UserDetailServiceImpl userService;

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(HttpServletRequest request, HttpServletResponse response) {

        // Cookie에서 Refresh Token 추출
        Cookie[] cookies = request.getCookies();
        if (cookies == null || cookies.length == 0) {
            return ResponseEntity.status(401)
                    .body(new ErrorResponse("리프레쉬 토큰 없음"));
        }


        String refreshToken = Arrays.stream(cookies)
                .filter(cookie -> "refreshToken".equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);

        if (refreshToken == null) {
            return ResponseEntity.status(401)
                    .body(new ErrorResponse("리프레쉬 토큰 없음"));
        }


        try {
            //  검증 및 새로운 리프레쉬 토큰 생성 (Rotation)
            RefreshResult refreshResult = refreshTokenService.refreshAccessToken(refreshToken);
            String newRefreshToken = refreshResult.newRefreshToken();
            String loginID = refreshResult.loginID();

            UserDetails userDetails = userService.loadUserByUsername(loginID);

            // 새로운 액세스 토큰 생성
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
            String newAccessToken = jwtUtils.generateAccessToken(authentication);

            // 새로운 리프레쉬 토큰을 Cookie에 저장 (기존 것 교체)
            Cookie newRefreshTokenCookie = new Cookie("refreshToken", newRefreshToken);
            newRefreshTokenCookie.setHttpOnly(true);  // XSS 방어
            newRefreshTokenCookie.setSecure(true);    // HTTPS만
            newRefreshTokenCookie.setPath("/");
            newRefreshTokenCookie.setMaxAge(14 * 24 * 60 * 60);  // 14일
            response.addCookie(newRefreshTokenCookie);

            // 새로운 Access Token 반환
            return ResponseEntity.ok(new TokenResponse(
                    true,
                    "토큰 리프레쉬",
                    newAccessToken
            ));

        } catch (ExpiredJwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse("세션이 만료되었습니다. 다시 로그인하세요."));
        } catch (SecurityException | MalformedJwtException e) {
            // 토큰 해킹 시도 가능성
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse("유효하지 않은 접근입니다."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("서버 오류가 발생했습니다."));
        }
    }


    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request,
                                    HttpServletResponse response,
                                    Authentication authentication) {

        if (authentication != null) {
            String loginID = authentication.getName();

            // DB에서 Refresh Token 삭제
            refreshTokenService.deleteRefreshToken(loginID);

            // Cookie에서 Refresh Token 삭제
            Cookie cookie = new Cookie("refreshToken", null);
            cookie.setMaxAge(0);  // 즉시 만료
            cookie.setPath("/");
            response.addCookie(cookie);

            return ResponseEntity.ok(new ApiResponse(true, "로그아웃 성공"));
        }


        return ResponseEntity.badRequest()
                .body(new ApiResponse(false, "인증되지 않은 사용자"));
    }
}
