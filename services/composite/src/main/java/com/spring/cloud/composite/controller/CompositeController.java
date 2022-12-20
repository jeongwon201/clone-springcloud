package com.spring.cloud.composite.controller;

import com.spring.cloud.api.controller.CompositeControllerInterface;
import com.spring.cloud.api.dto.*;
import com.spring.cloud.api.exception.NotFoundException;
import com.spring.cloud.api.util.ServiceUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@AllArgsConstructor
@RestController
public class CompositeController implements CompositeControllerInterface {

    private final IntegrateModule integrateModule;
    private final ServiceUtil serviceUtil;

    @Override
    public void createComposite(Composite body) {
        try {
            Product product = new Product(body.getProductId(), body.getProductName(), null, null);
            integrateModule.createProduct(product);

            if(body.getRecommendList() != null) {
                body.getRecommendList().forEach(r -> {
                    Recommend recommend = new Recommend(body.getProductId(), r.getRecommendId(), r.getAuthor(), r.getContent(), null);
                    integrateModule.createRecommend(recommend);
                });
            }

            if(body.getReviewList() != null) {
                body.getReviewList().forEach(r -> {
                    Review review = new Review(body.getProductId(), r.getReviewId(), r.getAuthor(), r.getSubject(), r.getContent(), null);
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

        String productAddress = product.getServiceAddress();
        String recommendAddress = (recommends != null && recommends.size() > 0) ? recommends.get(0).getServiceAddress() : "";
        String reviewAddress = (reviews != null && reviews.size() > 0) ? reviews.get(0).getServiceAddress() : "";

        ServiceAddresses serviceAddresses = new ServiceAddresses(serviceUtil.getServiceAddress(), productAddress, recommendAddress, reviewAddress);
        return new Composite(product.getProductId(), product.getProductName(), recommends, reviews, serviceAddresses);
    }

    @Override
    public void deleteComposite(int productId) {
        integrateModule.deleteProduct(productId);
        integrateModule.deleteRecommends(productId);
        integrateModule.deleteReviews(productId);
    }
}
