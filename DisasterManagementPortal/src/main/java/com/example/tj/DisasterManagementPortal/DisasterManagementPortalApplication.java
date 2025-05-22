package com.example.tj.DisasterManagementPortal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = { org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class })
public class DisasterManagementPortalApplication {

	public static void main(String[] args) {
		SpringApplication.run(DisasterManagementPortalApplication.class, args);
	}

}
