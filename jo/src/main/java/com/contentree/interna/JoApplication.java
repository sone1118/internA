package com.contentree.interna;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class JoApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(applicationClass, args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(applicationClass);
	}

	private static Class<JoApplication> applicationClass = JoApplication.class;
}
