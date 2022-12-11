package com.spring.cloud.composite.controller;

import com.spring.cloud.api.dto.Product;
import com.spring.cloud.api.dto.Recommend;
import com.spring.cloud.api.dto.Review;
import com.spring.cloud.api.exception.InvalidInputException;
import com.spring.cloud.api.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@AutoConfigureWebTestClient
public class TestCompositeController {

    private static final int PRODUCT_ID_OK = 1;
    private static final int PRODUCT_ID_NOT_FOUND = 2;
    private static final int PRODUCT_ID_INVALID = 3;

    @Autowired
    private WebTestClient client;

    @MockBean
    private IntegrateModule integrateModule;

    @BeforeEach
    public void setUp() {
        when(integrateModule.getProduct(PRODUCT_ID_OK))
                .thenReturn(new Product(PRODUCT_ID_OK, "name", null));
        when(integrateModule.getRecommends(PRODUCT_ID_OK))
                .thenReturn(Collections.singletonList(new Recommend(PRODUCT_ID_OK, 1, "author", "content")));
        when(integrateModule.getReviews(PRODUCT_ID_OK))
                .thenReturn(Collections.singletonList(new Review(PRODUCT_ID_OK, 1, "author", "subject", "content")));

        when(integrateModule.getProduct(PRODUCT_ID_NOT_FOUND))
                .thenThrow(new NotFoundException("No productId: " + PRODUCT_ID_NOT_FOUND));

        when(integrateModule.getProduct(PRODUCT_ID_INVALID))
                .thenThrow(new InvalidInputException("Invalid productId: " + PRODUCT_ID_INVALID));
    }

    @Test
    public void composite() {
        String result = client.get()
                .uri("/composite/" + PRODUCT_ID_OK)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.productId").isEqualTo(PRODUCT_ID_OK)
                .jsonPath("$.recommendList.length()").isEqualTo(1)
                .jsonPath("$.reviewList.length()").isEqualTo(1)
                .returnResult().toString();

        System.out.println(result);
    }

    @Test
    public void composite_NOT_FOUND() {
        String result = client.get()
                .uri("/composite/" + PRODUCT_ID_NOT_FOUND)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.path").isEqualTo("/composite/" + PRODUCT_ID_NOT_FOUND)
                .jsonPath("$.message").isEqualTo("No productId: " + PRODUCT_ID_NOT_FOUND)
                .returnResult().toString();

        System.out.println(result);
    }

    @Test
    public void composite_INVALID() {
        String result = client.get()
                .uri("/composite/" + PRODUCT_ID_INVALID)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.path").isEqualTo("/composite/" + PRODUCT_ID_INVALID)
                .jsonPath("$.message").isEqualTo("Invalid productId: " + PRODUCT_ID_INVALID)
                .returnResult().toString();

        System.out.println(result);
    }
}
