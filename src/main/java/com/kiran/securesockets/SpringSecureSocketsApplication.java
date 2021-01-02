package com.kiran.securesockets;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SpringSecureSocketsApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringSecureSocketsApplication.class, args);
	}

}
