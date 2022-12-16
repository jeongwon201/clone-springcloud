package com.spring.cloud.product.domain;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.stream.IntStream.rangeClosed;

@ExtendWith(SpringExtension.class)
@DataMongoTest(properties = {"spring.data.mongodb.port=0"})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestProductRepository {

    @Autowired
    private ProductRepository repository;
    private ProductEntity savedEntity;

    private void assertEqualsProduct(ProductEntity expectedEntity, ProductEntity actualEntity) {
        Assertions.assertEquals(expectedEntity.getId(), actualEntity.getId());
        Assertions.assertEquals(expectedEntity.getVersion(), actualEntity.getVersion());
        Assertions.assertEquals(expectedEntity.getProductId(), actualEntity.getProductId());
        Assertions.assertEquals(expectedEntity.getProductName(), actualEntity.getProductName());
    }

    @BeforeEach
    public void setUpDB() {
        System.out.println("setUpDB");

        repository.deleteAll();
        ProductEntity entity = new ProductEntity(1, "1_name", "1_info");
        savedEntity = repository.save(entity);
        assertEqualsProduct(entity, savedEntity);
    }

    @Test
    @Order(1)
    public void _create() {
        ProductEntity newEntity = new ProductEntity(2, "2_name", "2_info");
        savedEntity = repository.save(newEntity);
        ProductEntity foundEntity = repository.findById(newEntity.getId()).get();
        assertEqualsProduct(newEntity, foundEntity);
        Assertions.assertEquals(2, repository.count());
    }

    @Test
    @Order(2)
    public void _update() {
        savedEntity.setProductName("name2-1");
        savedEntity = repository.save(savedEntity);
        ProductEntity foundEntity = repository.findById(savedEntity.getId()).get();
        Assertions.assertEquals(1, (long) foundEntity.getVersion());
        Assertions.assertEquals("name2-1", foundEntity.getProductName());
    }

    @Test
    @Order(3)
    public void _delete() {
        repository.delete(savedEntity);
        Assertions.assertEquals(false, repository.existsById(savedEntity.getId()));
    }

    @Test
    @Order(4)
    public void _getByProductId() {
        Optional<ProductEntity> entity = repository.findByProductId(savedEntity.getProductId());
        Assertions.assertEquals(true, entity.isPresent());
        assertEqualsProduct(savedEntity, entity.get());
    }

    @Test
    @Order(5)
    public void _duplicateError() {
        Assertions.assertThrows(DuplicateKeyException.class, () -> {
            ProductEntity entity = new ProductEntity(savedEntity.getProductId(), "name", "info");
            repository.save(entity);
        });
    }

    @Test
    @Order(6)
    public void _optimisticLockError() {
        ProductEntity entity1 = repository.findById(savedEntity.getId()).get();
        ProductEntity entity2 = repository.findById(savedEntity.getId()).get();

        entity1.setProductName("n1");
        repository.save(entity1);

        try {
            entity2.setProductName("n2");
            repository.save(entity2);
        } catch (OptimisticLockingFailureException e) {
            e.printStackTrace();
        }

        ProductEntity updateEntity = repository.findById(savedEntity.getId()).get();
        Assertions.assertEquals(1, updateEntity.getVersion());
        Assertions.assertEquals("n1", updateEntity.getProductName());
    }

    @Test
    @Order(7)
    public void _paging() {
        repository.deleteAll();
        List<ProductEntity> newProducts = rangeClosed(1001, 1010)
                .mapToObj(i -> new ProductEntity(i, i + "_name", i + "_info")).collect(Collectors.toList());
        repository.saveAll(newProducts);

        Pageable nextPage = PageRequest.of(0, 4, Sort.Direction.ASC, "productId");

        nextPage = testNextPage(nextPage, "[1001, 1002, 1003, 1004]", true);
        nextPage = testNextPage(nextPage, "[1005, 1006, 1007, 1008]", true);
        nextPage = testNextPage(nextPage, "[1009, 1010]", false);

    }

    private Pageable testNextPage(Pageable nextPage, String expectedProductIds, boolean expectsNextPage) {
        Page<ProductEntity> productPage = repository.findAll(nextPage);
        Assertions.assertEquals(expectedProductIds, productPage.getContent().stream().map(p -> p.getProductId()).collect(Collectors.toList()).toString());
        Assertions.assertEquals(expectsNextPage, productPage.hasNext());
        return productPage.nextPageable();
    }
}
