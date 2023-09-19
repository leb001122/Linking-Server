package com.linking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.TimeZone;

@Slf4j
@SpringBootApplication
public class LinkingApplication {

	public static void main(String[] args) {
		SpringApplication.run(LinkingApplication.class, args);
	}

	@PostConstruct
	public void setTimeZone() {
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
		log.info("LocalDateTime.now() = {}", LocalDateTime.now());
	}
}