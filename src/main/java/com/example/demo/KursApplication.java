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
		Call call = new Call();
		call.setName("Adam");
		return call;
	}

	@Bean(name = "callMichal")
	Call call2(){
		Call call = new Call();
		call.setName("Michał");
		return call;
	}

}
