package com.agronotify;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AgroNotifyApplication {

	public static void main(String[] args) {
		SpringApplication.run(AgroNotifyApplication.class, args);
	}

}
