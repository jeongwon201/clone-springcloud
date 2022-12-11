package com.spring.cloud.composite.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.cloud.api.controller.ProductControllerInterface;
import com.spring.cloud.api.controller.RecommendControllerInterface;
import com.spring.cloud.api.controller.ReviewControllerInterface;
import com.spring.cloud.api.dto.Product;
import com.spring.cloud.api.dto.Recommend;
import com.spring.cloud.api.dto.Review;
import com.spring.cloud.api.exception.InvalidInputException;
import com.spring.cloud.api.exception.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.HttpMethod.GET;

@Slf4j
@Component
public class IntegrateModule implements ProductControllerInterface, RecommendControllerInterface, ReviewControllerInterface {

    private final RestTemplate restTemplate;
    private final ObjectMapper mapper;

    private final String productServiceUrl;
    private final String recommendServiceUrl;
    private final String reviewServiceUrl;

    public IntegrateModule(
            RestTemplate restTemplate,
            ObjectMapper mapper,

            @Value("${app.product.protocol}") String productProtocol,
            @Value("${app.product.host}") String productHost,
            @Value("${app.product.port}") int productPort,
            @Value("${app.product.service}") String productService,

            @Value("${app.recommend.protocol}") String recommendProtocol,
            @Value("${app.recommend.host}") String recommendHost,
            @Value("${app.recommend.port}") int recommendPort,
            @Value("${app.recommend.service}") String recommendService,

            @Value("${app.review.protocol}") String reviewProtocol,
            @Value("${app.review.host}") String reviewHost,
            @Value("${app.review.port}") int reviewPort,
            @Value("${app.review.service}") String reviewService
            ) {
        this.restTemplate = restTemplate;
        this.mapper = mapper;

        productServiceUrl = productProtocol + "://" + productHost + ":" + productPort + "/" + productService;
        recommendServiceUrl = recommendProtocol + "://" + recommendHost + ":" + recommendPort + "/" + recommendService;
        reviewServiceUrl = reviewProtocol + "://" + reviewHost + ":" + reviewPort + "/" + reviewService;

        log.debug("==========");
        log.debug(productServiceUrl);
        log.debug(recommendServiceUrl);
        log.debug(reviewServiceUrl);
        log.debug("==========");
    }

    private RuntimeException httpClientException(HttpClientErrorException e) {
        switch (e.getStatusCode()) {
            case NOT_FOUND:
                return new NotFoundException(e.getResponseBodyAsString());
            case UNPROCESSABLE_ENTITY:
                return new InvalidInputException(e.getResponseBodyAsString());
            default:
                log.warn("Got a unexpected HTTP error: {}, will rethrow it", e.getStatusCode());
                log.warn("Error body: {}", e.getResponseBodyAsString());
                return e;
        }
    }

    @Override
    public Product createProduct(Product body) {
        try {
            String url = productServiceUrl;
            Product product = restTemplate.postForObject(url, body, Product.class);
            log.debug("##############################################################################################");
            log.debug("createProduct: {}", url);
            log.debug("product: {}", product.toString());
            log.debug("##############################################################################################");
            return product;
        } catch (HttpClientErrorException e) {
            throw httpClientException(e);
        }
    }

    @Override
    public Product getProduct(int productId) {
        try {
            String url = productServiceUrl + "/" + productId;
            Product product = restTemplate.getForObject(url, Product.class);
            log.debug("##############################################################################################");
            log.debug("getProduct: {}", url);
            log.debug("product: {}", product.toString());
            log.debug("##############################################################################################");
            return product;
        } catch (HttpClientErrorException e) {
            throw httpClientException(e);
        }
    }

    @Override
    public void deleteProduct(int productId) {
        try {
            String url = productServiceUrl + "/" + productId;
            restTemplate.delete(url);
            log.debug("##############################################################################################");
            log.debug("deleteProduct: {}", url);
            log.debug("productId: {}", productId);
            log.debug("##############################################################################################");
        } catch (HttpClientErrorException e) {
            throw httpClientException(e);
        }
    }

    @Override
    public Recommend createRecommend(Recommend body) {
        try {
            String url = recommendServiceUrl;
            Recommend recommend = restTemplate.postForObject(url, body, Recommend.class);
            log.debug("##############################################################################################");
            log.debug("createRecommend: {}", url);
            log.debug("recommend: {}", recommend.toString());
            log.debug("##############################################################################################");
            return recommend;
        } catch (HttpClientErrorException e) {
            throw httpClientException(e);
        }
    }

    @Override
    public List<Recommend> getRecommends(int productId) {
        try {
            String url = recommendServiceUrl + "?productId=" + productId;
            List<Recommend> recommends = restTemplate.exchange(url, GET, null, new ParameterizedTypeReference<List<Recommend>>() {}).getBody();
            log.debug("##############################################################################################");
            log.debug("getRecommend: {}", url);
            log.debug("productId: {}, recommends size: {}", productId, recommends.size());
            log.debug("##############################################################################################");
            return recommends;
        } catch (HttpClientErrorException e) {
            log.warn("getRecommends exception, error msg: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public void deleteRecommends(int productId) {
        try {
            String url = recommendServiceUrl + "?productId=" + productId;
            restTemplate.delete(url);
            log.debug("##############################################################################################");
            log.debug("deleteRecommend: {}", url);
            log.debug("productId: {}", productId);
            log.debug("##############################################################################################");
        } catch (HttpClientErrorException e) {
            throw httpClientException(e);
        }
    }

    @Override
    public Review createReview(Review body) {
        try {
            String url = reviewServiceUrl;
            Review review = restTemplate.postForObject(url, body, Review.class);
            log.debug("##############################################################################################");
            log.debug("createReview: {}", url);
            log.debug("review: {}", review.toString());
            log.debug("##############################################################################################");
            return review;
        } catch (HttpClientErrorException e) {
            throw httpClientException(e);
        }
    }

    @Override
    public List<Review> getReviews(int productId) {
        try {
            String url = reviewServiceUrl + "?productId=" + productId;
            List<Review> reviews = restTemplate.exchange(url, GET, null, new ParameterizedTypeReference<List<Review>>() {}).getBody();
            log.debug("##############################################################################################");
            log.debug("getReview: {}", url);
            log.debug("productId: {}, reviews size: {}", productId, reviews.size());
            log.debug("##############################################################################################");
            return reviews;
        } catch (HttpClientErrorException e) {
            log.warn("getReviews exception, error msg: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public void deleteReviews(int productId) {
        try {
            String url = reviewServiceUrl + "?productId=" + productId;
            restTemplate.delete(url);
            log.debug("##############################################################################################");
            log.debug("deleteReview: {}", url);
            log.debug("productId: {}", productId);
            log.debug("##############################################################################################");
        } catch (HttpClientErrorException e) {
            throw httpClientException(e);
        }
    }
}