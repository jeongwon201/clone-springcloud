package com.spring.cloud.review.controller;

import com.spring.cloud.api.dto.Recommend;
import com.spring.cloud.api.dto.Review;
import com.spring.cloud.review.domain.ReviewEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

    @Mappings({
            @Mapping(target = "serviceAddress", ignore = true)
    })
    Review entityToDto(ReviewEntity entity);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "version", ignore = true)
    })
    ReviewEntity dtoToEntity(Review dto);

    List<Review> entityListToApiList(List<ReviewEntity> entity);
    List<ReviewEntity> dtoListToEntityList(List<Review> dto);
}
