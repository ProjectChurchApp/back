package com.church.app.Security.config;

import com.church.app.Security.login.filter.JwtAuthFilter;
import com.church.app.Security.login.filter.LoginAuthFilter;
import com.church.app.Security.login.provider.LoginAuthProvider;
import com.church.app.Security.login.service.RefreshTokenService;
import com.church.app.Security.login.utils.JwtUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;

@Configuration
public class SecurityConfig {

    private final LoginAuthProvider loginAuthProvider;
    private final JwtUtils jwtUtils;
    private final RefreshTokenService refreshTokenService;
    private final UserDetailsService userDetailsService;

    public SecurityConfig(LoginAuthProvider loginAuthProvider,
                          JwtUtils jwtUtils,
                          RefreshTokenService refreshTokenService,
                          UserDetailsService userDetailsService) {
        this.loginAuthProvider = loginAuthProvider;
        this.jwtUtils = jwtUtils;
        this.refreshTokenService = refreshTokenService;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public AuthenticationManager authenticationManager() {
            return new ProviderManager(List.of(loginAuthProvider));
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationManager authenticationManager) throws Exception {

        LoginAuthFilter loginAuthFilter = new LoginAuthFilter(
                authenticationManager
                ,jwtUtils
                ,refreshTokenService
        );

        JwtAuthFilter jwtAuthFilter = new JwtAuthFilter(jwtUtils,userDetailsService);

        http
                .addFilterAt(loginAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .formLogin(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .csrf(AbstractHttpConfigurer::disable)

                .cors(Customizer.withDefaults())

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/signup", "/auth/login", "/auth/refresh").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll() // ← 추가
                        .anyRequest().authenticated()
                );
        return http.build();
    }

}
