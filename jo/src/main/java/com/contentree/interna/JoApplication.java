package com.contentree.interna;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
// Spring Boot 2.x
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;


@SpringBootApplication
//@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
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
