package com.spring.cloud.api.controller;

import com.spring.cloud.api.dto.Product;
import org.springframework.web.bind.annotation.*;

public interface ProductControllerInterface {

    @PostMapping(value = "/product", consumes = "application/json", produces = "application/json")
    Product createProduct(@RequestBody Product body);

    @GetMapping(value = "/product/{productId}", produces = "application/json")
    Product getProduct(@PathVariable int productId);

    @DeleteMapping(value = "/product/{productId}")
    void deleteProduct(@PathVariable int productId);
}