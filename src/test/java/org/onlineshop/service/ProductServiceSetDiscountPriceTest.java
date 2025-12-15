package org.onlineshop.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.onlineshop.dto.product.ProductResponseDto;
import org.onlineshop.entity.Category;
import org.onlineshop.entity.Product;
import org.onlineshop.exception.NotFoundException;
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

    @Test
    void testSetDiscountPriceIfOk() {
        Category categoryFirst = Category.builder()
                .categoryName("testCategoryFirst")
                .image("https://drive.google.com/file/first")
                .products(new ArrayList<>())
                .build();

        categoryRepository.save(categoryFirst);

        Product productTestOne = Product.builder()
                .name("testProductOne")
                .category(categoryFirst)
                .description("testDescription")
                .price(BigDecimal.valueOf(100))
                .discountPrice(BigDecimal.valueOf(10))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .image("https://drive.google.com/file/first")
                .build();

        productRepository.save(productTestOne);

        ProductResponseDto updateDiscountPriceProduct = productService.setDiscountPrice(productTestOne.getId(), BigDecimal.valueOf(100));
        assertEquals(updateDiscountPriceProduct.getProductName(), productTestOne.getName());
        assertEquals(updateDiscountPriceProduct.getProductDiscountPrice(), BigDecimal.valueOf(100));
        assertEquals(1, productRepository.findAll().size());
    }

    @Test
    void testSetDiscountPriceIfProductNotFound() {
        Exception exception = assertThrows(NotFoundException.class, () -> productService.setDiscountPrice(100000, BigDecimal.valueOf(100)));
        String messageException = "Product with id = 100000 not found";
        assertEquals(messageException, exception.getMessage());
    }

    @Test
    void testSetDiscountPriceIfNewDiscountPriceIsNull() {
        Category category = Category.builder()
                .categoryName("testCategory")
                .image("https://drive.google.com/file/test")
                .products(new ArrayList<>())
                .build();

        categoryRepository.save(category);

        Product product = Product.builder()
                .name("testProduct")
                .category(category)
                .description("testDescription")
                .price(BigDecimal.valueOf(100))
                .discountPrice(BigDecimal.valueOf(10))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .image("https://drive.google.com/file/test")
                .build();

        productRepository.save(product);

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> productService.setDiscountPrice(product.getId(), null));
        String messageException = "New discount price cannot be null";
        assertEquals(messageException, exception.getMessage());
    }

    @Test
    void testSetDiscountPriceIfNewDiscountPriceIsNegative() {
        Category category = Category.builder()
                .categoryName("testCategory")
                .image("https://drive.google.com/file/test")
                .products(new ArrayList<>())
                .build();

        categoryRepository.save(category);

        Product product = Product.builder()
                .name("testProduct")
                .category(category)
                .description("testDescription")
                .price(BigDecimal.valueOf(100))
                .discountPrice(BigDecimal.valueOf(10))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .image("https://drive.google.com/file/test")
                .build();

        productRepository.save(product);

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> productService.setDiscountPrice(product.getId(), BigDecimal.valueOf(-10)));
        String messageException = "New discount price cannot be less than 0";
        assertEquals(messageException, exception.getMessage());
    }

    @Test
    void testSetDiscountPriceIfNewDiscountPriceIsZero() {
        Category category = Category.builder()
                .categoryName("testCategory")
                .image("https://drive.google.com/file/test")
                .products(new ArrayList<>())
                .build();

        categoryRepository.save(category);

        Product product = Product.builder()
                .name("testProduct")
                .category(category)
                .description("testDescription")
                .price(BigDecimal.valueOf(100))
                .discountPrice(BigDecimal.valueOf(10))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .image("https://drive.google.com/file/test")
                .build();

        productRepository.save(product);

        ProductResponseDto result = productService.setDiscountPrice(product.getId(), BigDecimal.ZERO);

        assertEquals(BigDecimal.ZERO, result.getProductDiscountPrice());
        assertEquals("testProduct", result.getProductName());
    }
}