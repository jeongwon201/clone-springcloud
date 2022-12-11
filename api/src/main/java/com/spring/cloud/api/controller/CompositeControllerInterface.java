package com.spring.cloud.api.controller;

import com.spring.cloud.api.dto.Composite;
import org.springframework.web.bind.annotation.*;

public interface CompositeControllerInterface {

    @PostMapping(value = "/composite", consumes = "application/json")
    void createComposite(@RequestBody Composite body);

    @GetMapping(value = "/composite/{productId}", produces = "application/json")
    Composite getComposite(@PathVariable int productId);

    @DeleteMapping(value = "/composite/{productId}")
    void deleteComposite(@PathVariable int productId);
}