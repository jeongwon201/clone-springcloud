package com.spring.cloud.recommend.domain;


import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface RecommendRepository extends CrudRepository<RecommendEntity, String> {

    @Transactional(readOnly = true)
    List<RecommendEntity> findByProductId(int ProductId);
}