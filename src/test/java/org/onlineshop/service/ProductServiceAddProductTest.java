package org.onlineshop.service;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.onlineshop.dto.product.ProductRequestDto;
import org.onlineshop.dto.product.ProductResponseDto;
import org.onlineshop.entity.Category;
import org.onlineshop.entity.Product;
import org.onlineshop.exception.BadRequestException;
import org.onlineshop.exception.NotFoundException;
import org.onlineshop.exception.UrlValidationError;
import org.onlineshop.exception.UrlValidationException;
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

        Category category = Category.builder()
                .categoryName("testCategory")
                .image("https://drive.google.com/file/d/1y03Ct0ABP1X8O6NFvK6FdqiMacYpLeTs/view?usp=drive_link")
                .products(new ArrayList<>())
                .build();

        categoryRepository.save(category);

        Product productTestOne = Product.builder()
                .name("TestProductSecond")
                .category(category)
                .description("testDescription")
                .price(BigDecimal.valueOf(100))
                .discountPrice(BigDecimal.valueOf(10))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .image("https://drive.google.com/file/first")
                .build();

        productRepository.save(productTestOne);

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
        assertEquals(2, productRepository.findAll().size());

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

        Exception exception = assertThrows(NotFoundException.class, () -> productService.addProduct(requestDto));
        assertEquals("Category with name: " + requestDto.getProductCategory() + " not found", exception.getMessage());

    }

    @Test
    void testAddProductWhenCategoryAlreadyContainsProductWithSameName() {
        ProductRequestDto productSecond = ProductRequestDto.builder()
                .productName("TestProductSecond")
                .productCategory("testCategory")
                .image("https://drive.google.com/Dublicate")
                .productDescription("TestProductTextForUniqueTest")
                .productPrice(BigDecimal.valueOf(80))
                .productDiscountPrice(BigDecimal.valueOf(10))
                .build();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> productService.addProduct(productSecond));

        String message = exception.getMessage();
        assertTrue(message.contains("TestProductSecond"));
        assertTrue(message.contains("testCategory"));
    }

    @Test
    void testAddProductIfPriceIsZero() {
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

    @Test
    void testAddProductIfPriceIsNull() {
        ProductRequestDto product = ProductRequestDto.builder()
                .productName("TestProduct")
                .productCategory("testCategory")
                .image("https://drive.google.com/test")
                .productDescription("TestProductText")
                .productPrice(null)
                .productDiscountPrice(BigDecimal.valueOf(5))
                .build();

        Set<ConstraintViolation<ProductRequestDto>> violations = validatorFactory.getValidator().validate(product);
        assertFalse(violations.isEmpty(), "Validation should fail for price = null");
        assertTrue(
                violations.stream().anyMatch(v -> v.getMessage().equals("Price cannot be null")),
                "Error message should be 'Price cannot be null'"
        );

    }

    @Test
    void testAddProductIfNameIsBlank() {
        ProductRequestDto product = ProductRequestDto.builder()
                .productName(" ")
                .productCategory("testCategory")
                .image("https://drive.google.com/test")
                .productDescription("TestProductText")
                .productPrice(BigDecimal.valueOf(100))
                .productDiscountPrice(BigDecimal.valueOf(5))
                .build();

        Set<ConstraintViolation<ProductRequestDto>> violations = validatorFactory.getValidator().validate(product);
        assertFalse(violations.isEmpty(), "Validation should fail for blank name");
        assertTrue(
                violations.stream().anyMatch(v -> v.getMessage().equals("Product title is required and must be not blank")),
                "Error message should be 'Product title is required and must be not blank'"
        );

    }

    @Test
    void testAddProductIfNameIsTooShort() {
        ProductRequestDto product = ProductRequestDto.builder()
                .productName("Te")
                .productCategory("testCategory")
                .image("https://drive.google.com/test")
                .productDescription("TestProductText")
                .productPrice(BigDecimal.valueOf(100))
                .productDiscountPrice(BigDecimal.valueOf(5))
                .build();

        Set<ConstraintViolation<ProductRequestDto>> violations = validatorFactory.getValidator().validate(product);
        assertFalse(violations.isEmpty(), "Validation should fail for too short name");
        assertTrue(
                violations.stream().anyMatch(v -> v.getMessage().equals("Product title must be between 3 and 20 characters")),
                "Error message should be 'Product title must be between 3 and 20 characters'"
        );

    }

    @Test
    void testAddProductIfNameIsTooLong() {
        ProductRequestDto product = ProductRequestDto.builder()
                .productName("TestNameTestNameTestNameTestNameTestNameTestNameTestNameTestNameTestNameTestNameTestName")
                .productCategory("testCategory")
                .image("https://drive.google.com/test")
                .productDescription("TestProductText")
                .productPrice(BigDecimal.valueOf(100))
                .productDiscountPrice(BigDecimal.valueOf(5))
                .build();

        Set<ConstraintViolation<ProductRequestDto>> violations = validatorFactory.getValidator().validate(product);
        assertFalse(violations.isEmpty(), "Validation should fail for too long name");
        assertTrue(
                violations.stream().anyMatch(v -> v.getMessage().equals("Product title must be between 3 and 20 characters")),
                "Error message should be 'Product title must be between 3 and 20 characters'"
        );

    }

    @Test
    void testAddProductIfCategoryIsBlank() {
        ProductRequestDto product = ProductRequestDto.builder()
                .productName("TestName")
                .productCategory(" ")
                .image("https://drive.google.com/test")
                .productDescription("TestProductText")
                .productPrice(BigDecimal.valueOf(100))
                .productDiscountPrice(BigDecimal.valueOf(5))
                .build();

        Set<ConstraintViolation<ProductRequestDto>> violations = validatorFactory.getValidator().validate(product);
        assertFalse(violations.isEmpty(), "Validation should fail for blank category");
        assertTrue(
                violations.stream().anyMatch(v -> v.getMessage().equals("Product category is required and must be not blank")),
                "Error message should be 'Product category is required and must be not blank'"
        );

    }
    @Test
    void testAddProductIfImageUrlInvalid() {
        ProductRequestDto product = ProductRequestDto.builder()
                .productName("TestName")
                .productCategory("testCategory")
                .image("abc")
                .productDescription("TestProductText")
                .productPrice(BigDecimal.valueOf(100))
                .productDiscountPrice(BigDecimal.valueOf(5))
                .build();

        UrlValidationException ex = assertThrows(
                UrlValidationException.class,
                () -> productService.addProduct(product),
                "Service should throw UrlValidationException for invalid image URL"
        );
        UrlValidationError err = ex.getError();
        assertTrue(
                err == UrlValidationError.INVALID_DOMAIN
                        || err == UrlValidationError.INVALID_EXTENSION
                        || err == UrlValidationError.UNREACHABLE,
                () -> "Unexpected error type for invalid image URL: " + err
        );
    }

}