package com.spring.cloud.review;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {"spring.datasource.url=jdbc:h2:mem:review-db"})
class ReviewApplicationTests {

    @Test
    void contextLoads() {
    }

}
