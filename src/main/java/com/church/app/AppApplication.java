package com.church.app;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
public class AppApplication {

	// 엔티티가 LocalDateTime.now()로 시각을 기록하므로 JVM 기본 시간대를 KST로 고정한다.
	@PostConstruct
	public void setDefaultTimeZone() {
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
	}

	public static void main(String[] args) {
		SpringApplication.run(AppApplication.class, args);
	}

}
