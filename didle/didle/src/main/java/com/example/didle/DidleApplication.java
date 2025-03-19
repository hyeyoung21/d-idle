package com.example.didle;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DidleApplication {

    public static void main(String[] args) {
        System.out.println("🔍 Environment Variables:");
        System.out.println(" - SPRING_DATASOURCE_URL: " + System.getenv("SPRING_DATASOURCE_URL"));
        System.out.println(" - SPRING_DATASOURCE_USERNAME: " + System.getenv("SPRING_DATASOURCE_USERNAME"));
        System.out.println(" - SPRING_DATASOURCE_PASSWORD: " + System.getenv("SPRING_DATASOURCE_PASSWORD"));

        System.out.println("\n🔍 Application Properties:");
        System.out.println(" - spring.datasource.url: " + System.getProperty("spring.datasource.url"));
        System.out.println(" - spring.datasource.username: " + System.getProperty("spring.datasource.username"));
        System.out.println(" - spring.datasource.password: " + System.getProperty("spring.datasource.password"));

        SpringApplication.run(DidleApplication.class, args);
    }

}
