package com.example.didle;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource(properties = {
        "spring.cloud.aws.credentials.access-key=test-access-key",
        "spring.cloud.aws.credentials.secret-key=test-secret-key",
        "spring.cloud.aws.region.static=us-east-2",
})

@SpringBootTest
@ActiveProfiles("test")
class DidleApplicationTests {

    @Test
    void contextLoads() {

    }

}

