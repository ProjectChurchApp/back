package com.church.app.Security.login.utils;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Component
public class JwtUtils {

    @Value("${jwt.secret}")
    private String jwtSecret;

    // accessToken 15분
    @Value("${jwt.access-expiration}")
    private long accessTokenExpiration;

    // RefreshToken 14일
    @Value("${jwt.refresh-expiration}")
    private long refreshTokenExpiration;

    private SecretKey getSigningKey() {
        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    //액세스 토큰
    public String generateAccessToken(Authentication authentication) {
        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();

        return Jwts.builder()
                .subject(userPrincipal.getUsername())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + accessTokenExpiration))
                .signWith(getSigningKey())
                .compact();
    }


    public boolean validateAccessToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (SecurityException e) {
            log.error("JWT 서명이 유효하지않음: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.error("JWT 토큰이 유효하지않음: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("JWT 토큰이 만료되었습니다: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("JWT 토큰이 지원되지 않습니다: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims 비어 있습니다: {}", e.getMessage());
        }
        return false;
    }


    public String getUserNameFromAccessToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    //리프레쉬 토큰
    public String generateRefreshToken() {
        return UUID.randomUUID().toString();
    }


    public long getRefreshTokenExpirationInMillis() {
        return refreshTokenExpiration;
    }

    // 버전 불일치시 호환성 유지를 위해 만들어둠
    @Deprecated
    public String generateJwtToken(Authentication authentication) {
        return generateAccessToken(authentication);
    }


    @Deprecated
    public String getUserNameFromJwtToken(String token) {
        return getUserNameFromAccessToken(token);
    }


    @Deprecated
    public boolean validateJwtToken(String authToken) {
        return validateAccessToken(authToken);
    }
}
