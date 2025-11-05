package org.onlineshop.service;

import jakarta.validation.ValidatorFactory;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.yml")
class ProductServiceSetDiscountPriceTest {

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
    void testSetDiscountPriceIfOk() {
        ProductRequestDto productFirst = ProductRequestDto.builder()
                .productName("TestProduct")
                .productCategory("testCategoryFirst")
                .image("https://drive.google.com/test")
                .productDescription("TestProductText")
                .productPrice(BigDecimal.valueOf(100))
                .productDiscountPrice(BigDecimal.valueOf(5))
                .build();

        ProductResponseDto savedProductFirst = productService.addProduct(productFirst);

        ProductResponseDto updateDiscountPriceProduct = productService.setDiscountPrice(savedProductFirst.getProductId(), BigDecimal.valueOf(100));
        assertEquals(updateDiscountPriceProduct.getProductName(), productFirst.getProductName());
        assertEquals(updateDiscountPriceProduct.getProductDiscountPrice(), BigDecimal.valueOf(100));
        assertEquals(1, productRepository.findAll().size());
    }

    @Test
    void testSetDiscountPriceIfProductNotFound() {

        Exception exception = assertThrows(IllegalArgumentException.class, () -> productService.setDiscountPrice(10000,BigDecimal.valueOf(100)));
        String messageException = "Product with id = 10000 not found";
        assertEquals(messageException, exception.getMessage());
    }
}