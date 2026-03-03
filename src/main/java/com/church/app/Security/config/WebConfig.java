/*
package com.church.app.Security.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")

                // 허용할 HTTP 메서드
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")

                // 허용할 헤더
                .allowedHeaders("*")

                //Authorization 헤더를 클라이언트에 노출
                .exposedHeaders("Authorization")

                // Preflight 요청 캐시 시간 (1시간)
                .maxAge(3600);
    }
}*/
