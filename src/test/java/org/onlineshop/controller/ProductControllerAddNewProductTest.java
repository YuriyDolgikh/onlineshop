package org.onlineshop.controller;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.onlineshop.dto.product.ProductRequestDto;
import org.onlineshop.dto.product.ProductResponseDto;
import org.onlineshop.entity.Category;
import org.onlineshop.entity.Product;
import org.onlineshop.entity.User;
import org.onlineshop.exception.BadRequestException;
import org.onlineshop.exception.UrlValidationException;
import org.onlineshop.repository.CategoryRepository;
import org.onlineshop.repository.ProductRepository;
import org.onlineshop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.yml")
class ProductControllerAddNewProductTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductController productController;

    @AfterEach
    void dropDatabase() {
        productRepository.deleteAll();
        categoryRepository.deleteAll();
        userRepository.deleteAll();
    }

    @BeforeEach
    void setUp() {
        User newTestUser = User.builder()
                .username("newTestUser")
                .email("testUser@email.com")
                .hashPassword("$2a$10$WiAt7dmC1vLIxjY9/9n7P.I5RQU1MKKSOI1Dy1pNLPPIts7K5RJR2")
                .phoneNumber("+494949494949")
                .status(User.Status.CONFIRMED)
                .role(User.Role.USER)
                .build();

        userRepository.save(newTestUser);

        Category category = Category.builder()
                .categoryName("testCategory")
                .image("https://drive.google.com/file/d/1y03Ct0ABP1X8O6NFvK6FdqiMacYpLeTs/view?usp=drive_link")
                .products(new ArrayList<>())
                .build();

        categoryRepository.save(category);
    }

    @Test
    @WithMockUser(username = "testUser@email.com", roles = "ADMIN")
    void testAddNewProductIfRoleAdminAndOk() {
        ProductRequestDto requestDto = ProductRequestDto.builder()
                .productName("TestProduct")
                .productCategory("testCategory")
                .image("https://drive.google.com/test")
                .productDescription("TestProductText")
                .productPrice(BigDecimal.valueOf(100))
                .productDiscountPrice(BigDecimal.valueOf(5))
                .build();

        ResponseEntity<ProductResponseDto> response = productController.addNewProduct(requestDto);

        assertNotNull(response);
        assertEquals(201, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(1, productRepository.findAll().size());
        assertEquals("TestProduct", response.getBody().getProductName());
    }

    @Test
    @WithMockUser(username = "testUser@email.com", roles = "MANAGER")
    void testAddNewProductIfRoleManagerAndOk() {
        ProductRequestDto requestDto = ProductRequestDto.builder()
                .productName("TestProduct")
                .productCategory("testCategory")
                .image("https://drive.google.com/test")
                .productDescription("TestProductText")
                .productPrice(BigDecimal.valueOf(100))
                .productDiscountPrice(BigDecimal.valueOf(5))
                .build();

        ResponseEntity<ProductResponseDto> response = productController.addNewProduct(requestDto);

        assertNotNull(response);
        assertEquals(201, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(1, productRepository.findAll().size());
        assertEquals("TestProduct", response.getBody().getProductName());
    }

    @Test
    @WithMockUser(username = "testUser@email.com", roles = "USER")
    void testAddNewProductIfRoleUser() {
        ProductRequestDto requestDto = ProductRequestDto.builder()
                .productName("TestProduct")
                .productCategory("testCategory")
                .image("https://drive.google.com/test")
                .productDescription("TestProductText")
                .productPrice(BigDecimal.valueOf(100))
                .productDiscountPrice(BigDecimal.valueOf(5))
                .build();

        Exception exception = assertThrows(AuthorizationDeniedException.class, () -> productController.addNewProduct(requestDto));
        String messageException = "Access Denied";
        assertEquals(messageException, exception.getMessage());
        assertEquals(0, productRepository.findAll().size());
    }

    @Test
    @WithMockUser(username = "testUser@email.com", roles = "ADMIN")
    void testAddNewProductIfRoleAdminAndProductNameIsBlank() {
        ProductRequestDto requestDto = ProductRequestDto.builder()
                .productName(" ")
                .productCategory("testCategory")
                .image("https://drive.google.com/test")
                .productDescription("TestProductText")
                .productPrice(BigDecimal.valueOf(100))
                .productDiscountPrice(BigDecimal.valueOf(5))
                .build();

        assertThrows(ConstraintViolationException.class, () -> productController.addNewProduct(requestDto));
        assertEquals(0, productRepository.findAll().size());
    }

    @Test
    @WithMockUser(username = "testUser@email.com", roles = "MANAGER")
    void testAddNewProductIfRoleManagerAndProductNameIsBlank() {
        ProductRequestDto requestDto = ProductRequestDto.builder()
                .productName(" ")
                .productCategory("testCategory")
                .image("https://drive.google.com/test")
                .productDescription("TestProductText")
                .productPrice(BigDecimal.valueOf(100))
                .productDiscountPrice(BigDecimal.valueOf(5))
                .build();

        assertThrows(ConstraintViolationException.class, () -> productController.addNewProduct(requestDto));
        assertEquals(0, productRepository.findAll().size());
    }

    @Test
    @WithMockUser(username = "testUser@email.com", roles = "ADMIN")
    void testAddNewProductIfRoleAdminAndProductCategoryIsBlank() {
        ProductRequestDto requestDto = ProductRequestDto.builder()
                .productName("TestProduct")
                .productCategory(" ")
                .image("https://drive.google.com/test")
                .productDescription("TestProductText")
                .productPrice(BigDecimal.valueOf(100))
                .productDiscountPrice(BigDecimal.valueOf(5))
                .build();

        assertThrows(BadRequestException.class, () -> productController.addNewProduct(requestDto));
        assertEquals(0, productRepository.findAll().size());
    }

    @Test
    @WithMockUser(username = "testUser@email.com", roles = "MANAGER")
    void testAddNewProductIfRoleManagerAndProductCategoryIsBlank() {
        ProductRequestDto requestDto = ProductRequestDto.builder()
                .productName("TestProduct")
                .productCategory(" ")
                .image("https://drive.google.com/test")
                .productDescription("TestProductText")
                .productPrice(BigDecimal.valueOf(100))
                .productDiscountPrice(BigDecimal.valueOf(5))
                .build();

        assertThrows(BadRequestException.class, () -> productController.addNewProduct(requestDto));
        assertEquals(0, productRepository.findAll().size());
    }

    @Test
    @WithMockUser(username = "testUser@email.com", roles = "ADMIN")
    void testAddNewProductIfRoleAdminAndProductNameIsNull() {
        ProductRequestDto requestDto = ProductRequestDto.builder()
                .productName(null)
                .productCategory("testCategory")
                .image("https://drive.google.com/test")
                .productDescription("TestProductText")
                .productPrice(BigDecimal.valueOf(100))
                .productDiscountPrice(BigDecimal.valueOf(5))
                .build();

        assertThrows(NullPointerException.class, () -> productController.addNewProduct(requestDto));
        assertEquals(0, productRepository.findAll().size());
    }

    @Test
    @WithMockUser(username = "testUser@email.com", roles = "MANAGER")
    void testAddNewProductIfRoleManagerAndProductNameIsNull() {
        ProductRequestDto requestDto = ProductRequestDto.builder()
                .productName(null)
                .productCategory("testCategory")
                .image("https://drive.google.com/test")
                .productDescription("TestProductText")
                .productPrice(BigDecimal.valueOf(100))
                .productDiscountPrice(BigDecimal.valueOf(5))
                .build();

        assertThrows(NullPointerException.class, () -> productController.addNewProduct(requestDto));
        assertEquals(0, productRepository.findAll().size());
    }

    @Test
    @WithMockUser(username = "testUser@email.com", roles = "ADMIN")
    void testAddNewProductIfRoleAdminAndProductCategoryIsNull() {
        ProductRequestDto requestDto = ProductRequestDto.builder()
                .productName("TestProduct")
                .productCategory(null)
                .image("https://drive.google.com/test")
                .productDescription("TestProductText")
                .productPrice(BigDecimal.valueOf(100))
                .productDiscountPrice(BigDecimal.valueOf(5))
                .build();

        assertThrows(BadRequestException.class, () -> productController.addNewProduct(requestDto));
        assertEquals(0, productRepository.findAll().size());
    }

    @Test
    @WithMockUser(username = "testUser@email.com", roles = "MANAGER")
    void testAddNewProductIfRoleManagerAndProductCategoryIsNull() {
        ProductRequestDto requestDto = ProductRequestDto.builder()
                .productName("TestProduct")
                .productCategory(null)
                .image("https://drive.google.com/test")
                .productDescription("TestProductText")
                .productPrice(BigDecimal.valueOf(100))
                .productDiscountPrice(BigDecimal.valueOf(5))
                .build();

        assertThrows(BadRequestException.class, () -> productController.addNewProduct(requestDto));
        assertEquals(0, productRepository.findAll().size());
    }

    @Test
    @WithMockUser(username = "testUser@email.com", roles = "ADMIN")
    void testAddNewProductIfRoleAdminAndProductPriceIsNull() {
        ProductRequestDto requestDto = ProductRequestDto.builder()
                .productName("TestProduct")
                .productCategory("testCategory")
                .image("https://drive.google.com/test")
                .productDescription("TestProductText")
                .productPrice(null)
                .productDiscountPrice(BigDecimal.valueOf(5))
                .build();

        assertThrows(DataIntegrityViolationException.class, () -> productController.addNewProduct(requestDto));
        assertEquals(0, productRepository.findAll().size());
    }

    @Test
    @WithMockUser(username = "testUser@email.com", roles = "MANAGER")
    void testAddNewProductIfRoleManagerAndProductPriceIsNull() {
        ProductRequestDto requestDto = ProductRequestDto.builder()
                .productName("TestProduct")
                .productCategory("testCategory")
                .image("https://drive.google.com/test")
                .productDescription("TestProductText")
                .productPrice(null)
                .productDiscountPrice(BigDecimal.valueOf(5))
                .build();

        assertThrows(DataIntegrityViolationException.class, () -> productController.addNewProduct(requestDto));
        assertEquals(0, productRepository.findAll().size());
    }

    @Test
    @WithMockUser(username = "testUser@email.com", roles = "ADMIN")
    void testAddNewProductIfRoleAdminAndProductPriceIsZero() {
        ProductRequestDto requestDto = ProductRequestDto.builder()
                .productName("TestProduct")
                .productCategory("testCategory")
                .image("https://drive.google.com/test")
                .productDescription("TestProductText")
                .productPrice(BigDecimal.ZERO)
                .productDiscountPrice(BigDecimal.valueOf(5))
                .build();

        assertThrows(ConstraintViolationException.class, () -> productController.addNewProduct(requestDto));
        assertEquals(0, productRepository.findAll().size());
    }

    @Test
    @WithMockUser(username = "testUser@email.com", roles = "ADMIN")
    void testAddNewProductIfRoleAdminAndWhenCategoryAlreadyContainsProductWithSameName() {
        Category categorySecond = Category.builder()
                .categoryName("testCategorySecond")
                .image("https://drive.google.com/file/d/1y03Ct0ABP1X8O6NFvK6FdqiMacYpLeTs/view?usp=drive_link")
                .products(new ArrayList<>())
                .build();

        categoryRepository.save(categorySecond);

        Product productTestOne = Product.builder()
                .name("TestProductSecond")
                .category(categorySecond)
                .description("testDescription")
                .price(BigDecimal.valueOf(100))
                .discountPrice(BigDecimal.valueOf(10))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .image("https://drive.google.com/file/first")
                .build();

        productRepository.save(productTestOne);

        ProductRequestDto requestDto = ProductRequestDto.builder()
                .productName("TestProductSecond")
                .productCategory("testCategorySecond")
                .image("https://drive.google.com/test")
                .productDescription("TestProductText")
                .productPrice(BigDecimal.valueOf(100))
                .productDiscountPrice(BigDecimal.valueOf(5))
                .build();

        assertThrows(IllegalArgumentException.class, () -> productController.addNewProduct(requestDto));
        assertEquals(1, productRepository.findAll().size());
    }

    @Test
    @WithMockUser(username = "testUser@email.com", roles = "MANAGER")
    void testAddNewProductIfRoleManagerAndWhenCategoryAlreadyContainsProductWithSameName() {
        Category categorySecond = Category.builder()
                .categoryName("testCategorySecond")
                .image("https://drive.google.com/file/d/1y03Ct0ABP1X8O6NFvK6FdqiMacYpLeTs/view?usp=drive_link")
                .products(new ArrayList<>())
                .build();

        categoryRepository.save(categorySecond);

        Product productTestOne = Product.builder()
                .name("TestProductSecond")
                .category(categorySecond)
                .description("testDescription")
                .price(BigDecimal.valueOf(100))
                .discountPrice(BigDecimal.valueOf(10))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .image("https://drive.google.com/file/first")
                .build();

        productRepository.save(productTestOne);

        ProductRequestDto requestDto = ProductRequestDto.builder()
                .productName("TestProductSecond")
                .productCategory("testCategorySecond")
                .image("https://drive.google.com/test")
                .productDescription("TestProductText")
                .productPrice(BigDecimal.valueOf(100))
                .productDiscountPrice(BigDecimal.valueOf(5))
                .build();

        assertThrows(IllegalArgumentException.class, () -> productController.addNewProduct(requestDto));
        assertEquals(1, productRepository.findAll().size());
    }

    @Test
    @WithMockUser(username = "testUser@email.com", roles = "ADMIN")
    void testAddNewProductIfRoleAdminAndProductImageIsNotValid() {
        ProductRequestDto requestDto = ProductRequestDto.builder()
                .productName("TestProduct")
                .productCategory("testCategory")
                .image("NOT VALID IMAGE")
                .productDescription("TestProductText")
                .productPrice(BigDecimal.valueOf(100))
                .productDiscountPrice(BigDecimal.valueOf(5))
                .build();

        assertThrows(UrlValidationException.class, () -> productController.addNewProduct(requestDto));
        assertEquals(0, productRepository.findAll().size());
    }
}