package com.delivery_tracking_system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DeliveryTrackingSystemApplication {
	public static void main(String[] args) {
		SpringApplication.run(DeliveryTrackingSystemApplication.class, args);
	}
}