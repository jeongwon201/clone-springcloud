package com.spring.cloud.recommend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {"spring.data.mongodb.port=0"})
class RecommendApplicationTests {

    @Test
    void contextLoads() {
    }

}
