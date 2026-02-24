package com.church.app.Security.config;

import com.church.app.Security.login.filter.LoginAuthFilter;
import com.church.app.Security.login.provider.LoginAuthProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;

@Configuration
public class SecurityConfig {

    private final LoginAuthProvider loginAuthProvider;

    public SecurityConfig(LoginAuthProvider loginAuthProvider) {
        this.loginAuthProvider = loginAuthProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager() {
            return new ProviderManager(List.of(loginAuthProvider));
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationManager authenticationManager) throws Exception {

        LoginAuthFilter loginAuthFilter = new LoginAuthFilter(
                authenticationManager
        );
        http
                .addFilterAt(loginAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .formLogin(AbstractHttpConfigurer::disable)

                .csrf(AbstractHttpConfigurer::disable)

                .cors(Customizer.withDefaults())

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/signup", "/auth/login").permitAll()
                        .anyRequest().authenticated()
                );
        return http.build();
    }

}
