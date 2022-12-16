package com.spring.cloud.recommend.controller;

import com.spring.cloud.api.dto.Recommend;
import com.spring.cloud.recommend.domain.RecommendRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = RANDOM_PORT, properties = {"spring.data.mongodb.port=0"})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureWebTestClient
public class TestRecommendController {

    @Autowired
    private WebTestClient client;

    @Autowired
    private RecommendRepository repository;

    @BeforeEach
    public void setUpDB() {
        repository.deleteAll();
    }



    private WebTestClient.BodyContentSpec addRecommend(int productId, int recommendId, HttpStatus expectedStatus) {
        Recommend recommend = new Recommend(productId, recommendId, "Author " + recommendId, "Content " + recommendId);
        WebTestClient.BodyContentSpec result = client.post()
                .uri("/recommend")
                .body(Mono.just(recommend), Recommend.class)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(expectedStatus)
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody();

        return result;
    }

    private WebTestClient.BodyContentSpec getRecommends(String query, HttpStatus expectedStatus) {
        return client.get()
                .uri("/recommend" + query)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(expectedStatus)
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody();
    }

    private WebTestClient.BodyContentSpec deleteRecommend(int productId, HttpStatus expectedStatus) {
        return client.delete()
                .uri("/recommend?productId=" + productId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(expectedStatus)
                .expectBody();
    }

    @Test
    @Order(1)
    public void _getRecommends() {
        int productId = 1;
        addRecommend(productId, 1, OK);
        addRecommend(productId, 2, OK);
        addRecommend(productId, 3, OK);

        Assertions.assertEquals(3, repository.findByProductId(productId).size());

        getRecommends("?productId=" + productId, OK)
                .jsonPath("$.length()").isEqualTo(3)
                .jsonPath("$[2].productId").isEqualTo(productId)
                .jsonPath("$[2].recommendId").isEqualTo(3);
    }

    @Test
    @Order(2)
    public void _duplicateRecommendError() {
        int productId = 1;
        int recommendId = 1;

        addRecommend(productId, recommendId, OK)
                .jsonPath("$.productId").isEqualTo(productId)
                .jsonPath("$.recommendId").isEqualTo(recommendId);
        Assertions.assertEquals(1, repository.count());

        addRecommend(productId, recommendId, UNPROCESSABLE_ENTITY)
                .jsonPath("$.path").isEqualTo("/recommend")
                .jsonPath("$.message").isEqualTo("Duplicate key, Product Id: 1, Recommend Id: 1");
        Assertions.assertEquals(1, repository.count());
    }

    @Test
    @Order(3)
    public void _deleteRecommend() {
        int productId = 1;
        int recommendId = 1;

        addRecommend(productId, recommendId, OK);
        Assertions.assertEquals(1, repository.findByProductId(productId).size());

        deleteRecommend(productId, OK);
        Assertions.assertEquals(0, repository.findByProductId(productId).size());
    }

    @Test
    @Order(4)
    public void _setRecommendMissingParameter() {
        getRecommends("", BAD_REQUEST)
                .jsonPath("$.path").isEqualTo("/recommend")
                .jsonPath("$.message").isEqualTo("Required int parameter 'productId' is not present");
    }

    @Test
    @Order(5)
    public void _setRecommendInvalidParameter() {
        getRecommends("?productId=", BAD_REQUEST)
                .jsonPath("$.path").isEqualTo("/recommend")
                .jsonPath("$.message").isEqualTo("Type mismatch.");
    }

    @Test
    @Order(6)
    public void _getRecommendsNotFound() {
        getRecommends("?productId=1004", OK)
                .jsonPath("$.length()").isEqualTo(0);
    }
}
