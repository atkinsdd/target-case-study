package com.ddatkins;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@SpringBootApplication
@ComponentScan("com.ddatkins")
public class TargetCaseStudyApplication {

	public static void main(String[] args) {
		SpringApplication.run(TargetCaseStudyApplication.class, args);
	}
}
