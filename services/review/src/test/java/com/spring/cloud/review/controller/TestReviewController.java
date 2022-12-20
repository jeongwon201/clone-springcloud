package com.spring.cloud.review.controller;

import com.spring.cloud.api.dto.Review;
import com.spring.cloud.review.domain.ReviewRepository;
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
@SpringBootTest(webEnvironment = RANDOM_PORT, properties = {"spring.datasource.url=jdbc:h2:mem:review-db", "eureka.client.enabled=false"})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureWebTestClient
public class TestReviewController {

    @Autowired
    private WebTestClient client;

    @Autowired
    private ReviewRepository repository;

    @BeforeEach
    public void setUpDB() {
        repository.deleteAll();
    }

    private WebTestClient.BodyContentSpec getReviews(String query, HttpStatus expectedStatus) {
        return client.get()
                .uri("/review" + query)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(expectedStatus)
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody();
    }

    private WebTestClient.BodyContentSpec addReview(String productId, int reviewId, HttpStatus expectedStatus) {
        Review review = new Review(Integer.parseInt(productId), reviewId, "Author " + reviewId, "Subject " + reviewId, "Content " + reviewId, null);
        WebTestClient.BodyContentSpec result = client.post()
                .uri("/review")
                .body(Mono.just(review), Review.class)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(expectedStatus)
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody();

        return result;
    }

    private WebTestClient.BodyContentSpec deleteReview(int productId, HttpStatus expectedStatus) {
        return client.delete()
                .uri("/review?productId=" + productId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(expectedStatus)
                .expectBody();
    }

    @Test
    @Order(1)
    public void _getReviews() {
        int productId = 1;

        Assertions.assertEquals(0, repository.findByProductId(productId).size());

        addReview(String.valueOf(productId), 1, OK);
        addReview(String.valueOf(productId), 2, OK);
        addReview(String.valueOf(productId), 3, OK);

        Assertions.assertEquals(3, repository.findByProductId(productId).size());

        getReviews("?productId=" + productId, OK)
                .jsonPath("$.length()").isEqualTo(3)
                .jsonPath("$[2].productId").isEqualTo(productId)
                .jsonPath("$[2].reviewId").isEqualTo(3);
    }

    @Test
    @Order(2)
    public void _duplicateReviewError() {
        int productId = 1;
        int reviewId = 1;

        Assertions.assertEquals(0, repository.count());

        addReview(String.valueOf(productId), reviewId, OK)
                .jsonPath("$.productId").isEqualTo(productId)
                .jsonPath("$.reviewId").isEqualTo(reviewId);
        Assertions.assertEquals(1, repository.count());

        addReview(String.valueOf(productId), reviewId, UNPROCESSABLE_ENTITY)
                .jsonPath("$.path").isEqualTo("/review")
                .jsonPath("$.message").isEqualTo("Duplicate key, Product Id: 1, Review Id: 1");
        Assertions.assertEquals(1, repository.count());
    }

    @Test
    @Order(3)
    public void _deleteReview() {
        int productId = 1;
        int recommendId = 1;

        addReview(String.valueOf(productId), recommendId, OK);
        Assertions.assertEquals(1, repository.findByProductId(productId).size());

        deleteReview(productId, OK);
        Assertions.assertEquals(0, repository.findByProductId(productId).size());

        deleteReview(productId, OK);
    }
}
