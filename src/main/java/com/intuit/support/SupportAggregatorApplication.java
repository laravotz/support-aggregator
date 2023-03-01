package com.intuit.support;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SupportAggregatorApplication {
	public static void main(String[] args) {
		SpringApplication.run(SupportAggregatorApplication.class, args);
	}
}
