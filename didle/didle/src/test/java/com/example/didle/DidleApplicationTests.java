package com.example.didle;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource(properties = {
        "spring.cloud.aws.credentials.access-key=test-access-key",
        "spring.cloud.aws.credentials.secret-key=test-secret-key",
        "spring.cloud.aws.region.static=us-east-2",
        "spring.s3.bucket=test-bucket"
})

@SpringBootTest
@ActiveProfiles("test")
class DidleApplicationTests {

    @Test
    void contextLoads() {
        System.out.println("🔍 Environment Variables:");
        System.out.println(" - SPRING_DATASOURCE_URL: " + System.getenv("SPRING_DATASOURCE_URL"));
        System.out.println(" - SPRING_DATASOURCE_USERNAME: " + System.getenv("SPRING_DATASOURCE_USERNAME"));
        System.out.println(" - SPRING_DATASOURCE_PASSWORD: " + System.getenv("SPRING_DATASOURCE_PASSWORD"));

        System.out.println("\n🔍 Application Properties:");
        System.out.println(" - spring.datasource.url: " + System.getProperty("spring.datasource.url"));
        System.out.println(" - spring.datasource.username: " + System.getProperty("spring.datasource.username"));
        System.out.println(" - spring.datasource.password: " + System.getProperty("spring.datasource.password"));
    }

}

