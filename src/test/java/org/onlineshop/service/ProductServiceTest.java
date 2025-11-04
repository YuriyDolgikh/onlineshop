package org.onlineshop.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.onlineshop.dto.product.ProductRequestDto;
import org.onlineshop.entity.Category;
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

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.yml")
class ProductServiceTest {

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
        LocalDateTime now = LocalDateTime.now();

        Category category = Category.builder()
                .categoryName("testCategory")
                .image("https://drive.google.com/file/d/1y03Ct0ABP1X8O6NFvK6FdqiMacYpLeTs/view?usp=drive_link")
                .products(new ArrayList<>())
                .build();

        categoryRepository.save(category);

    }

    @Test
    void addProduct() {

        ProductRequestDto requestDto = ProductRequestDto.builder()
                .productName("TestProduct")
                .productCategory("testCategory")
                .image("https://drive.google.com/test")
                .productDescription("TestProductText")
                .productPrice(BigDecimal.valueOf(100))
                .productDiscountPrice(BigDecimal.valueOf(5))
                .build();

        productService.addProduct(requestDto);
        assertEquals("TestProduct", requestDto.getProductName());
        assertEquals("testCategory", requestDto.getProductCategory());

    }
}