package org.onlineshop.service;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.onlineshop.dto.product.ProductRequestDto;
import org.onlineshop.dto.product.ProductResponseDto;
import org.onlineshop.dto.product.ProductUpdateDto;
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
import java.util.ArrayList;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.yml")
class ProductServiceUpdateProductTest {

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

        Category categoryFirst = Category.builder()
                .categoryName("testCategoryFirst")
                .image("https://drive.google.com/file/first")
                .products(new ArrayList<>())
                .build();

        categoryRepository.save(categoryFirst);

        Category categorySecond = Category.builder()
                .categoryName("testCategorySecond")
                .image("https://drive.google.com/file/second")
                .products(new ArrayList<>())
                .build();

        categoryRepository.save(categorySecond);

    }

    @Test
    void testUpdateProductNameIfOk() {
        ProductRequestDto requestDto = ProductRequestDto.builder()
                .productName("TestProduct")
                .productCategory("testCategoryFirst")
                .image("https://drive.google.com/test")
                .productDescription("TestProductText")
                .productPrice(BigDecimal.valueOf(100))
                .productDiscountPrice(BigDecimal.valueOf(5))
                .build();

        ProductResponseDto savedProductFirst = productService.addProduct(requestDto);

        ProductUpdateDto updateDto = ProductUpdateDto.builder()
                .productName("Product")
                .build();

        ProductResponseDto savedProductSecond = productService.updateProduct(savedProductFirst.getProductId(), updateDto);
        assertEquals(savedProductSecond.getProductName(), updateDto.getProductName());
        assertEquals(1, productRepository.findAll().size());
    }

    @Test
    void testUpdateProductCategoryIfOk() {
        ProductRequestDto requestDto = ProductRequestDto.builder()
                .productName("TestProduct")
                .productCategory("testCategoryFirst")
                .image("https://drive.google.com/test")
                .productDescription("TestProductText")
                .productPrice(BigDecimal.valueOf(100))
                .productDiscountPrice(BigDecimal.valueOf(5))
                .build();

        ProductResponseDto savedProductFirst = productService.addProduct(requestDto);

        ProductUpdateDto updateDto = ProductUpdateDto.builder()
                .productCategory("testCategorySecond")
                .build();

        ProductResponseDto savedProductSecond = productService.updateProduct(savedProductFirst.getProductId(), updateDto);
        assertEquals(savedProductSecond.getProductCategory(), updateDto.getProductCategory());
        assertEquals(1, productRepository.findAll().size());
    }

    @Test
    void testUpdateProductDescriptionIfOk() {
        ProductRequestDto requestDto = ProductRequestDto.builder()
                .productName("TestProduct")
                .productCategory("testCategoryFirst")
                .image("https://drive.google.com/test")
                .productDescription("TestProductText")
                .productPrice(BigDecimal.valueOf(100))
                .productDiscountPrice(BigDecimal.valueOf(5))
                .build();

        ProductResponseDto savedProductFirst = productService.addProduct(requestDto);

        ProductUpdateDto updateDto = ProductUpdateDto.builder()
                .productDescription("Text")
                .build();

        ProductResponseDto savedProductSecond = productService.updateProduct(savedProductFirst.getProductId(), updateDto);
        assertEquals(savedProductSecond.getProductDescription(), updateDto.getProductDescription());
        assertEquals(1, productRepository.findAll().size());
    }

    @Test
    void testUpdateProductPriceIfOk() {
        ProductRequestDto requestDto = ProductRequestDto.builder()
                .productName("TestProduct")
                .productCategory("testCategoryFirst")
                .image("https://drive.google.com/test")
                .productDescription("TestProductText")
                .productPrice(BigDecimal.valueOf(100))
                .productDiscountPrice(BigDecimal.valueOf(5))
                .build();

        ProductResponseDto savedProductFirst = productService.addProduct(requestDto);

        ProductUpdateDto updateDto = ProductUpdateDto.builder()
                .productPrice(BigDecimal.valueOf(50))
                .build();

        ProductResponseDto savedProductSecond = productService.updateProduct(savedProductFirst.getProductId(), updateDto);
        assertEquals(savedProductSecond.getProductPrice(), updateDto.getProductPrice());
        assertEquals(1, productRepository.findAll().size());
    }

    @Test
    void testUpdateProductDiscountPriceIfOk() {
        ProductRequestDto requestDto = ProductRequestDto.builder()
                .productName("TestProduct")
                .productCategory("testCategoryFirst")
                .image("https://drive.google.com/test")
                .productDescription("TestProductText")
                .productPrice(BigDecimal.valueOf(100))
                .productDiscountPrice(BigDecimal.valueOf(5))
                .build();

        ProductResponseDto savedProductFirst = productService.addProduct(requestDto);

        ProductUpdateDto updateDto = ProductUpdateDto.builder()
                .productDiscountPrice(BigDecimal.valueOf(20))
                .build();

        ProductResponseDto savedProductSecond = productService.updateProduct(savedProductFirst.getProductId(), updateDto);
        assertEquals(savedProductSecond.getProductDiscountPrice(), updateDto.getProductDiscountPrice());
        assertEquals(1, productRepository.findAll().size());
    }

    @Test
    void testUpdateProductImageIfOk() {
        ProductRequestDto requestDto = ProductRequestDto.builder()
                .productName("TestProduct")
                .productCategory("testCategoryFirst")
                .image("https://drive.google.com/test")
                .productDescription("TestProductText")
                .productPrice(BigDecimal.valueOf(100))
                .productDiscountPrice(BigDecimal.valueOf(5))
                .build();

        ProductResponseDto savedProductFirst = productService.addProduct(requestDto);

        ProductUpdateDto updateDto = ProductUpdateDto.builder()
                .image("https://drive.google.com/NotTest")
                .build();

        ProductResponseDto savedProductSecond = productService.updateProduct(savedProductFirst.getProductId(), updateDto);
        assertEquals(savedProductSecond.getImage(), updateDto.getImage());
        assertEquals(1, productRepository.findAll().size());
    }

    @Test
    void testUpdateProductWhenCategoryAlreadyContainsProductWithSameName() {
        ProductRequestDto productFirst = ProductRequestDto.builder()
                .productName("TestProduct")
                .productCategory("testCategoryFirst")
                .image("https://drive.google.com/test")
                .productDescription("TestProductText")
                .productPrice(BigDecimal.valueOf(100))
                .productDiscountPrice(BigDecimal.valueOf(5))
                .build();

        ProductResponseDto savedProductFirst = productService.addProduct(productFirst);

        ProductUpdateDto productSecond = ProductUpdateDto.builder()
                .productName("TestProduct")
                .build();


        Exception exception = assertThrows(IllegalArgumentException.class, () -> productService.updateProduct(savedProductFirst.getProductId(),productSecond ));
        String messageException = "Product with name: " + productSecond.getProductName() + " already exist in category.";
        assertEquals(messageException, exception.getMessage());

    }

    @Test
    void testUpdateProductWhenProductNotFound() {

        ProductUpdateDto productSecond = ProductUpdateDto.builder()
                .productName("Product")
                .build();

        Exception exception = assertThrows(IllegalArgumentException.class, () -> productService.updateProduct(10000,productSecond ));
        String messageException = "Product with id = 10000 not found";
        assertEquals(messageException, exception.getMessage());

    }

    @Test
    void testUpdateProductIfImageUrlInvalid() {
        ProductRequestDto productFirst = ProductRequestDto.builder()
                .productName("TestName")
                .productCategory("testCategoryFirst")
                .image("https://drive.google.com/test")
                .productDescription("TestProductText")
                .productPrice(BigDecimal.valueOf(100))
                .productDiscountPrice(BigDecimal.valueOf(5))
                .build();

        ProductResponseDto savedProductFirst = productService.addProduct(productFirst);

        ProductUpdateDto productSecond = ProductUpdateDto.builder()
                .image("INVALID")
                .build();

        Set<ConstraintViolation<ProductUpdateDto>> violations = validatorFactory.getValidator().validate(productSecond);
        assertFalse(violations.isEmpty(), "Validation should fail for invalid image");
        assertTrue(
                violations.stream().anyMatch(v -> v.getMessage().equals("Invalid image URL")),
                "Error message should be 'Invalid image URL'"
        );

    }

    @Test
    void testUpdateProductIfNameIsTooShort() {
        ProductRequestDto productFirst = ProductRequestDto.builder()
                .productName("TestProduct")
                .productCategory("testCategoryFirst")
                .image("https://drive.google.com/test")
                .productDescription("TestProductText")
                .productPrice(BigDecimal.valueOf(100))
                .productDiscountPrice(BigDecimal.valueOf(5))
                .build();

        ProductResponseDto savedProductFirst = productService.addProduct(productFirst);

        ProductUpdateDto productSecond = ProductUpdateDto.builder()
                .productName("Te")
                .build();

        Exception exception = assertThrows(IllegalArgumentException.class, () -> productService.updateProduct(savedProductFirst.getProductId(),productSecond ));
        String messageException = "Product title must be between 3 and 20 characters";
        assertEquals(messageException, exception.getMessage());

    }

    @Test
    void testUpdateProductIfNameIsTooLong() {
        ProductRequestDto productFirst = ProductRequestDto.builder()
                .productName("TestProduct")
                .productCategory("testCategoryFirst")
                .image("https://drive.google.com/test")
                .productDescription("TestProductText")
                .productPrice(BigDecimal.valueOf(100))
                .productDiscountPrice(BigDecimal.valueOf(5))
                .build();

        ProductResponseDto savedProductFirst = productService.addProduct(productFirst);

        ProductUpdateDto productSecond = ProductUpdateDto.builder()
                .productName("TestProductTestProductTestProductTestProductTestProductTestProductTestProductTestProductTestProductTestProductTestProductTestProductTestProduct")
                .build();

        Exception exception = assertThrows(IllegalArgumentException.class, () -> productService.updateProduct(savedProductFirst.getProductId(),productSecond ));
        String messageException = "Product title must be between 3 and 20 characters";
        assertEquals(messageException, exception.getMessage());

    }

    @Test
    void testUpdateProductIfPriceIsZero() {
        ProductRequestDto productFirst = ProductRequestDto.builder()
                .productName("TestProduct")
                .productCategory("testCategoryFirst")
                .image("https://drive.google.com/test")
                .productDescription("TestProductText")
                .productPrice(BigDecimal.valueOf(100))
                .productDiscountPrice(BigDecimal.valueOf(5))
                .build();

        ProductResponseDto savedProductFirst = productService.addProduct(productFirst);

        ProductUpdateDto productSecond = ProductUpdateDto.builder()
                .productPrice(BigDecimal.ZERO)
                .build();

        Exception exception = assertThrows(IllegalArgumentException.class, () -> productService.updateProduct(savedProductFirst.getProductId(),productSecond ));
        String messageException = "Product price must be greater than 0";
        assertEquals(messageException, exception.getMessage());

    }
}