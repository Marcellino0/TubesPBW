package com.example.m08;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class M08Application {

	public static void main(String[] args) {
		SpringApplication.run(M08Application.class, args);
	}

}
