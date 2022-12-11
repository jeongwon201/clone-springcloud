package com.spring.cloud.api.controller;

import com.spring.cloud.api.dto.Review;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public interface ReviewControllerInterface {

    @PostMapping(value = "/review", consumes = "application/json", produces = "application/json")
    Review createReview(@RequestBody Review body);

    @GetMapping(value = "/review", produces = "application/json")
    List<Review> getReviews(@RequestParam(value = "productId") int productId);

    @DeleteMapping(value = "/review")
    void deleteReviews(@RequestParam(value = "productId") int productId);
}
