package org.onlineshop.service;

import org.hibernate.validator.internal.constraintvalidators.bv.AssertFalseValidator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.onlineshop.dto.product.ProductRequestDto;
import org.onlineshop.dto.product.ProductResponseDto;
import org.onlineshop.entity.Category;
import org.onlineshop.repository.CategoryRepository;
import org.onlineshop.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
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

    @BeforeEach
    void setUp() {

        Category categoryFirst = Category.builder()
                .categoryName("testCategoryFirst")
                .image("https://drive.google.com/file/first")
                .products(new ArrayList<>())
                .build();

        categoryRepository.save(categoryFirst);

    }

    @Test
    void testDeleteProductIfOk() {
        ProductRequestDto productFirst = ProductRequestDto.builder()
                .productName("TestProduct")
                .productCategory("testCategoryFirst")
                .image("https://drive.google.com/test")
                .productDescription("TestProductText")
                .productPrice(BigDecimal.valueOf(100))
                .productDiscountPrice(BigDecimal.valueOf(5))
                .build();

        ProductResponseDto savedProductFirst = productService.addProduct(productFirst);

        productService.deleteProduct(savedProductFirst.getProductId());

        assertFalse(productRepository.findById(savedProductFirst.getProductId()).isPresent());
        assertEquals(0, productRepository.findAll().size());

    }

    @Test
    void testDeleteProductIfProductNotFound() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> productService.setDiscountPrice(10000,BigDecimal.valueOf(100)));
        String messageException = "Product with id = 10000 not found";
        assertEquals(messageException, exception.getMessage());

    }
}