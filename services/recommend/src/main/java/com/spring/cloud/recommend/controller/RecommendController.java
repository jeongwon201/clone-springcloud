package com.spring.cloud.recommend.controller;

import com.spring.cloud.api.controller.RecommendControllerInterface;
import com.spring.cloud.api.dto.Recommend;
import com.spring.cloud.api.exception.InvalidInputException;
import com.spring.cloud.api.util.ServiceUtil;
import com.spring.cloud.recommend.domain.RecommendEntity;
import com.spring.cloud.recommend.domain.RecommendRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
public class RecommendController implements RecommendControllerInterface {

    private final RecommendRepository repository;
    private final RecommendMapper mapper;
    private final ServiceUtil serviceUtil;

    @Override
    public Recommend createRecommend(Recommend body) {
        try {
            RecommendEntity entity = mapper.dtoToEntity(body);
            RecommendEntity newEntity = repository.save(entity);
            return mapper.entityToDto(newEntity);
        } catch (DuplicateKeyException e) {
            throw new InvalidInputException("Duplicate key, Product Id: " + body.getProductId() + ", Recommend Id: " + body.getRecommendId());
        }
    }

    @Override
    public List<Recommend> getRecommends(int productId) {
        if(productId < 1) throw new InvalidInputException("Invalid productId: " + productId);

        List<RecommendEntity> entityList = repository.findByProductId(productId);
        List<Recommend> list = mapper.entityListToApiList(entityList);

        if(list != null && list.size() > 0) {
            list.get(0).setServiceAddress(serviceUtil.getServiceAddress());
        }

        log.debug("getRecommends: response size: {}", list.size());

        return list;
    }

    @Override
    public void deleteRecommends(int productId) {
        repository.deleteAll(repository.findByProductId(productId));
    }
}
