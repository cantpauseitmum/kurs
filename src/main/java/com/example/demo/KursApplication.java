package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class KursApplication {

	public static void main(String[] args) {
		SpringApplication.run(KursApplication.class, args);
	}

	@Bean
	public Call call(){
		return new Call();
	}

}
