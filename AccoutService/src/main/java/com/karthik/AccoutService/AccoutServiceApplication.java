package com.karthik.AccoutService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class AccoutServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AccoutServiceApplication.class, args);
	}

}
