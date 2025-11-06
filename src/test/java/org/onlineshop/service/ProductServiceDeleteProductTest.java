package org.onlineshop.service;

import org.hibernate.validator.internal.constraintvalidators.bv.AssertFalseValidator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.onlineshop.dto.product.ProductRequestDto;
import org.onlineshop.dto.product.ProductResponseDto;
import org.onlineshop.entity.Category;
import org.onlineshop.entity.Product;
import org.onlineshop.repository.CategoryRepository;
import org.onlineshop.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.yml")
class ProductServiceDeleteProductTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @AfterEach
    void dropDatabase() {
        productRepository.deleteAll();
        categoryRepository.deleteAll();
    }

    @Test
    void testDeleteProductIfOk() {

        Category categoryFirst = Category.builder()
                .categoryName("testCategoryFirst")
                .image("https://drive.google.com/file/first")
                .products(new ArrayList<>())
                .build();

        categoryRepository.save(categoryFirst);

        Product productTest = Product.builder()
                .name("testProduct")
                .category(categoryFirst)
                .description("testDescription")
                .price(BigDecimal.valueOf(100))
                .discountPrice(BigDecimal.valueOf(10))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .image("https://drive.google.com/file/first")
                .build();

        productRepository.save(productTest);

        productService.deleteProduct(productTest.getId());

        assertFalse(productRepository.findById(productTest.getId()).isPresent());
        assertEquals(0, productRepository.findAll().size());

    }

    @Test
    void testDeleteProductIfProductNotFound() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> productService.setDiscountPrice(10000,BigDecimal.valueOf(100)));
        String messageException = "Product with id = 10000 not found";
        assertEquals(messageException, exception.getMessage());

    }
}