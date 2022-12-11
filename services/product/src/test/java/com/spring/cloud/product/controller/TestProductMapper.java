package com.spring.cloud.product.controller;

import com.spring.cloud.api.dto.Product;
import com.spring.cloud.product.domain.ProductEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

public class TestProductMapper {

    private ProductMapper mapper = Mappers.getMapper(ProductMapper.class);

    @Test
    public void mapperTests() {
        Assertions.assertNotNull(mapper);

        Product dto = new Product(1, "name", "info");
        ProductEntity entity = mapper.dtoToEntity(dto);

        Assertions.assertEquals(dto.getProductId(), entity.getProductId());
        Assertions.assertEquals(dto.getProductName(), entity.getProductName());
        Assertions.assertEquals(dto.getProductInfo(), entity.getProductInfo());

        Product dto2 = mapper.entityToDto(entity);

        Assertions.assertEquals(dto.getProductId(), dto2.getProductId());
        Assertions.assertEquals(dto.getProductName(), dto2.getProductName());
        Assertions.assertEquals(dto.getProductInfo(), dto2.getProductInfo());
    }
}
