package org.onlineshop.service;

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
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.yml")
class ProductServiceGetProductByCriteriaTest {

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
    void testGetProductsByCriteriaIfOk() {
        ProductRequestDto productFirst = ProductRequestDto.builder()
                .productName("TestProductFirst")
                .productCategory("testCategoryFirst")
                .image("https://drive.google.com/test")
                .productDescription("TestProductText")
                .productPrice(BigDecimal.valueOf(100))
                .productDiscountPrice(BigDecimal.valueOf(5))
                .build();

        ProductResponseDto savedProductFirst = productService.addProduct(productFirst);

        ProductRequestDto productSecond = ProductRequestDto.builder()
                .productName("TestProductSecond")
                .productCategory("testCategoryFirst")
                .image("https://drive.google.com/test")
                .productDescription("TestProductText")
                .productPrice(BigDecimal.valueOf(250))
                .productDiscountPrice(BigDecimal.valueOf(5))
                .build();

        ProductResponseDto savedProductSecond = productService.addProduct(productSecond);

        ProductRequestDto productThird = ProductRequestDto.builder()
                .productName("TestProductThird")
                .productCategory("testCategoryFirst")
                .image("https://drive.google.com/test")
                .productDescription("TestProductText")
                .productPrice(BigDecimal.valueOf(200))
                .productDiscountPrice(BigDecimal.valueOf(5))
                .build();

        ProductResponseDto savedProductThird = productService.addProduct(productThird);

       List<ProductResponseDto> result = productService.getProductsByCriteria("price","100-300","asc");

        assertEquals(3, result.size());
    }
}