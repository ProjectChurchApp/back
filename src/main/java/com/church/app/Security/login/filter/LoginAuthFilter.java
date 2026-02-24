package com.church.app.Security.login.filter;

import com.church.app.Security.login.dto.LoginRequest;
import com.church.app.Security.login.dto.LoginResponse;
import com.church.app.Security.login.service.RefreshTokenService;
import com.church.app.Security.login.utils.JwtUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.io.IOException;

public class LoginAuthFilter extends AbstractAuthenticationProcessingFilter {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final JwtUtils jwtUtils;
    private final RefreshTokenService refreshTokenService;
    public LoginAuthFilter(AuthenticationManager authenticationManager, JwtUtils jwtUtils1, RefreshTokenService refreshTokenService1) {
        super(new AntPathRequestMatcher("/auth/login", "POST"));
        this.jwtUtils = jwtUtils1;
        this.refreshTokenService = refreshTokenService1;
        this.setAuthenticationManager(authenticationManager);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {

        LoginRequest loginRequest = objectMapper.readValue(request.getInputStream(), LoginRequest.class);

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                loginRequest.loginID(),
                loginRequest.password()
        );

        return getAuthenticationManager().authenticate(authToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
            String loginID = authResult.getName();

            String role = authResult.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse("ROLE_ANONYMOUS");

            String accessToken = jwtUtils.generateAccessToken(authResult);

            String refreshToken = refreshTokenService.createRefreshToken(loginID);

            SecurityContextHolder.getContext().setAuthentication(authResult);

            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("application/json; charset=utf-8");

            response.addHeader("Authorization", "Bearer " + accessToken);

             Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
             refreshTokenCookie.setHttpOnly(true);  // XSS 방어
             refreshTokenCookie.setSecure(true);    // HTTPS만
             refreshTokenCookie.setPath("/");
             refreshTokenCookie.setMaxAge(14 * 24 * 60 * 60);  // 14일
             response.addCookie(refreshTokenCookie);

            LoginResponse loginResponse = new LoginResponse(
                    true,"로그인 성공", loginID,role,accessToken,refreshToken
            );

            response.getWriter().write(objectMapper.writeValueAsString(loginResponse));
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json; charset=utf-8");

            LoginResponse loginResponse = new LoginResponse(
                    false,"로그인 실패"+failed.getMessage(),null,null,null,null
            );

            response.getWriter().write(objectMapper.writeValueAsString(loginResponse));

    }
}
