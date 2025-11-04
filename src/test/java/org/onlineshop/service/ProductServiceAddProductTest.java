package org.onlineshop.service;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.onlineshop.dto.product.ProductRequestDto;
import org.onlineshop.dto.product.ProductResponseDto;
import org.onlineshop.entity.Category;
import org.onlineshop.exception.BadRequestException;
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
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.yml")
class ProductServiceAddProductTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ValidatorFactory validatorFactory;


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
    void testAddProductIfOk() {

        ProductRequestDto requestDto = ProductRequestDto.builder()
                .productName("TestProduct")
                .productCategory("testCategory")
                .image("https://drive.google.com/test")
                .productDescription("TestProductText")
                .productPrice(BigDecimal.valueOf(100))
                .productDiscountPrice(BigDecimal.valueOf(5))
                .build();

        ProductResponseDto savedProduct = productService.addProduct(requestDto);
        assertEquals(savedProduct.getProductName(), requestDto.getProductName());
        assertEquals(savedProduct.getProductCategory(), requestDto.getProductCategory());
        assertEquals(1, productRepository.findAll().size());

    }

    @Test
    void testAddProductIfCategoryNotFound() {

        ProductRequestDto requestDto = ProductRequestDto.builder()
                .productName("TestProduct")
                .productCategory("notTestCategory")
                .image("https://drive.google.com/test")
                .productDescription("TestProductText")
                .productPrice(BigDecimal.valueOf(100))
                .productDiscountPrice(BigDecimal.valueOf(5))
                .build();

        Exception exception = assertThrows(BadRequestException.class, () -> productService.addProduct(requestDto));
        assertEquals("Category with name: " + requestDto.getProductCategory() + " not found", exception.getMessage());

    }

    @Test
    void testAddProductWhenCategoryAlreadyContainsProductWithSameName() {
        ProductRequestDto productFirst = ProductRequestDto.builder()
                .productName("TestProduct")
                .productCategory("testCategory")
                .image("https://drive.google.com/test")
                .productDescription("TestProductText")
                .productPrice(BigDecimal.valueOf(100))
                .productDiscountPrice(BigDecimal.valueOf(5))
                .build();

        productService.addProduct(productFirst);

        ProductRequestDto productSecond = ProductRequestDto.builder()
                .productName("TestProduct")
                .productCategory("testCategory")
                .image("https://drive.google.com/Dublicate")
                .productDescription("TestProductTextForDublicate")
                .productPrice(BigDecimal.valueOf(80))
                .productDiscountPrice(BigDecimal.valueOf(10))
                .build();

        Exception exception = assertThrows(IllegalArgumentException.class, () -> productService.addProduct(productSecond));
        String messageException = "Product with name: " + productSecond.getProductName() + " already exist in category: " + productSecond.getProductCategory();
        assertEquals(messageException, exception.getMessage());

    }

    @Test
    void testAddProductIfPriceIsZero(){
        ProductRequestDto product = ProductRequestDto.builder()
                .productName("TestProduct")
                .productCategory("testCategory")
                .image("https://drive.google.com/test")
                .productDescription("TestProductText")
                .productPrice(BigDecimal.ZERO)
                .productDiscountPrice(BigDecimal.valueOf(5))
                .build();

        Set<ConstraintViolation<ProductRequestDto>> violations = validatorFactory.getValidator().validate(product);
        assertFalse(violations.isEmpty(), "Validation should fail for price = 0");
        assertTrue(
                violations.stream().anyMatch(v -> v.getMessage().equals("Price must be greater than 0")),
                "Error message should be 'Price must be greater than 0'"
        );

    }
}