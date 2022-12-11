package com.spring.cloud.composite.controller;

import com.spring.cloud.api.controller.CompositeControllerInterface;
import com.spring.cloud.api.dto.Composite;
import com.spring.cloud.api.dto.Product;
import com.spring.cloud.api.dto.Recommend;
import com.spring.cloud.api.dto.Review;
import com.spring.cloud.api.exception.NotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@AllArgsConstructor
@RestController
public class CompositeController implements CompositeControllerInterface {

    private final IntegrateModule integrateModule;

    @Override
    public void createComposite(Composite body) {
        try {
            Product product = new Product(body.getProductId(), body.getProductName(), null);
            integrateModule.createProduct(product);

            if(body.getRecommendList() != null) {
                body.getRecommendList().forEach(r -> {
                    Recommend recommend = new Recommend(body.getProductId(), r.getRecommendId(), r.getAuthor(), r.getContent());
                    integrateModule.createRecommend(recommend);
                });
            }

            if(body.getReviewList() != null) {
                body.getReviewList().forEach(r -> {
                    Review review = new Review(body.getProductId(), r.getReviewId(), r.getAuthor(), r.getSubject(), r.getContent());
                    integrateModule.createReview(review);
                });
            }
        } catch (RuntimeException e) {
            log.error("createComposite failed", e);
            throw e;
        }
    }

    @Override
    public Composite getComposite(int productId) {
        Product product = integrateModule.getProduct(productId);
        if(product == null) throw new NotFoundException("No productId: " + productId);

        List<Recommend> recommends = integrateModule.getRecommends(productId);

        List<Review> reviews = integrateModule.getReviews(productId);

        return new Composite(product.getProductId(), product.getProductName(), recommends, reviews);
    }

    @Override
    public void deleteComposite(int productId) {
        integrateModule.deleteProduct(productId);
        integrateModule.deleteRecommends(productId);
        integrateModule.deleteReviews(productId);
    }
}
