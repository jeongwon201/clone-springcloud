package com.spring.cloud.product;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {"spring.data.mongodb.port=0"})
class ProductApplicationTests {

    @Test
    void contextLoads() {
    }

}
