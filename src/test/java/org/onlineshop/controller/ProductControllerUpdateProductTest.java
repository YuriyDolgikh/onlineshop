package org.onlineshop.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.onlineshop.dto.product.ProductResponseDto;
import org.onlineshop.dto.product.ProductUpdateDto;
import org.onlineshop.entity.Category;
import org.onlineshop.entity.Product;
import org.onlineshop.entity.User;
import org.onlineshop.exception.UrlValidationException;
import org.onlineshop.repository.CategoryRepository;
import org.onlineshop.repository.ProductRepository;
import org.onlineshop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
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
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.yml")
class ProductControllerUpdateProductTest {
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
    }

    @Test
    @WithMockUser(username = "testUser@email.com",
            roles = {"ADMIN", "MANAGER"})
    void testUpdateProductNameIfRoleAdminAndManagerAndOk() {
        Category category = Category.builder()
                .categoryName("testCategory")
                .image("https://drive.google.com/file/d/1y03Ct0ABP1X8O6NFvK6FdqiMacYpLeTs/view?usp=drive_link")
                .products(new ArrayList<>())
                .build();

        categoryRepository.save(category);


        Product productTestOne = Product.builder()
                .name("TestProduct")
                .category(category)
                .description("testDescription")
                .price(BigDecimal.valueOf(100))
                .discountPrice(BigDecimal.valueOf(10))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .image("https://drive.google.com/file/first")
                .build();

        productRepository.save(productTestOne);

        ProductUpdateDto requestDto = ProductUpdateDto.builder()
                .productName("TestProductSecond")
                .build();

        ResponseEntity<ProductResponseDto> response = productController.updateProduct(productTestOne.getId(),requestDto);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(1, productRepository.findAll().size());
        assertEquals("TestProductSecond", response.getBody().getProductName());

    }

    @Test
    @WithMockUser(username = "testUser@email.com",
            roles = {"ADMIN", "MANAGER"})
    void testUpdateProductCategoryIfRoleAdminAndManagerAndOk() {
        Category category = Category.builder()
                .categoryName("testCategory")
                .image("https://drive.google.com/file/d/1y03Ct0ABP1X8O6NFvK6FdqiMacYpLeTs/view?usp=drive_link")
                .products(new ArrayList<>())
                .build();

        categoryRepository.save(category);

        Category categorySecond = Category.builder()
                .categoryName("testCategorySecond")
                .image("https://drive.google.com/file/d/1y03Ct0ABP1X8O6NFvK6FdqiMacYpLeTs/view?usp=drive_link")
                .products(new ArrayList<>())
                .build();

        categoryRepository.save(categorySecond);


        Product productTestOne = Product.builder()
                .name("TestProduct")
                .category(category)
                .description("testDescription")
                .price(BigDecimal.valueOf(100))
                .discountPrice(BigDecimal.valueOf(10))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .image("https://drive.google.com/file/first")
                .build();

        productRepository.save(productTestOne);

        ProductUpdateDto requestDto = ProductUpdateDto.builder()
                .productCategory("testCategorySecond")
                .build();

        ResponseEntity<ProductResponseDto> response = productController.updateProduct(productTestOne.getId(),requestDto);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(1, productRepository.findAll().size());
        assertEquals("testCategorySecond", response.getBody().getProductCategory());
    }

    @Test
    @WithMockUser(username = "testUser@email.com",
            roles = {"ADMIN", "MANAGER"})
    void testUpdateProductPriceIfRoleAdminAndManagerAndOk() {
        Category category = Category.builder()
                .categoryName("testCategory")
                .image("https://drive.google.com/file/d/1y03Ct0ABP1X8O6NFvK6FdqiMacYpLeTs/view?usp=drive_link")
                .products(new ArrayList<>())
                .build();

        categoryRepository.save(category);


        Product productTestOne = Product.builder()
                .name("TestProduct")
                .category(category)
                .description("testDescription")
                .price(BigDecimal.valueOf(100))
                .discountPrice(BigDecimal.valueOf(10))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .image("https://drive.google.com/file/first")
                .build();

        productRepository.save(productTestOne);

        ProductUpdateDto requestDto = ProductUpdateDto.builder()
                .productPrice(BigDecimal.valueOf(80))
                .build();

        ResponseEntity<ProductResponseDto> response = productController.updateProduct(productTestOne.getId(),requestDto);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(1, productRepository.findAll().size());
        assertEquals(BigDecimal.valueOf(80), response.getBody().getProductPrice());

    }

    @Test
    @WithMockUser(username = "testUser@email.com",
            roles = {"ADMIN", "MANAGER"})
    void testUpdateProductDiscountPriceIfRoleAdminAndManagerAndOk() {
        Category category = Category.builder()
                .categoryName("testCategory")
                .image("https://drive.google.com/file/d/1y03Ct0ABP1X8O6NFvK6FdqiMacYpLeTs/view?usp=drive_link")
                .products(new ArrayList<>())
                .build();

        categoryRepository.save(category);


        Product productTestOne = Product.builder()
                .name("TestProduct")
                .category(category)
                .description("testDescription")
                .price(BigDecimal.valueOf(100))
                .discountPrice(BigDecimal.valueOf(10))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .image("https://drive.google.com/file/first")
                .build();

        productRepository.save(productTestOne);

        ProductUpdateDto requestDto = ProductUpdateDto.builder()
                .productDiscountPrice(BigDecimal.valueOf(80))
                .build();

        ResponseEntity<ProductResponseDto> response = productController.updateProduct(productTestOne.getId(),requestDto);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(1, productRepository.findAll().size());
        assertEquals(BigDecimal.valueOf(80), response.getBody().getProductDiscountPrice());

    }

    @Test
    @WithMockUser(username = "testUser@email.com",
            roles = {"ADMIN", "MANAGER"})
    void testUpdateProductDescriptionIfRoleAdminAndManagerAndOk() {
        Category category = Category.builder()
                .categoryName("testCategory")
                .image("https://drive.google.com/file/d/1y03Ct0ABP1X8O6NFvK6FdqiMacYpLeTs/view?usp=drive_link")
                .products(new ArrayList<>())
                .build();

        categoryRepository.save(category);


        Product productTestOne = Product.builder()
                .name("TestProduct")
                .category(category)
                .description("testDescription")
                .price(BigDecimal.valueOf(100))
                .discountPrice(BigDecimal.valueOf(10))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .image("https://drive.google.com/file/first")
                .build();

        productRepository.save(productTestOne);

        ProductUpdateDto requestDto = ProductUpdateDto.builder()
                .productDescription("New Description")
                .build();

        ResponseEntity<ProductResponseDto> response = productController.updateProduct(productTestOne.getId(),requestDto);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(1, productRepository.findAll().size());
        assertEquals("New Description", response.getBody().getProductDescription());

    }

    @Test
    @WithMockUser(username = "testUser@email.com",
            roles = {"ADMIN", "MANAGER"})
    void testUpdateProductImageIfRoleAdminAndManagerAndOk() {
        Category category = Category.builder()
                .categoryName("testCategory")
                .image("https://drive.google.com/file/d/1y03Ct0ABP1X8O6NFvK6FdqiMacYpLeTs/view?usp=drive_link")
                .products(new ArrayList<>())
                .build();

        categoryRepository.save(category);


        Product productTestOne = Product.builder()
                .name("TestProduct")
                .category(category)
                .description("testDescription")
                .price(BigDecimal.valueOf(100))
                .discountPrice(BigDecimal.valueOf(10))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .image("https://drive.google.com/file/first")
                .build();

        productRepository.save(productTestOne);

        ProductUpdateDto requestDto = ProductUpdateDto.builder()
                .image("https://drive.google.com/file/new")
                .build();

        ResponseEntity<ProductResponseDto> response = productController.updateProduct(productTestOne.getId(),requestDto);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(1, productRepository.findAll().size());
        assertEquals("https://drive.google.com/file/new", response.getBody().getImage());

    }

    @Test
    @WithMockUser(username = "testUser@email.com",
            roles = {"ADMIN", "MANAGER"})
    void testUpdateProductNameIfRoleAdminAndManagerAndAndWhenCategoryAlreadyContainsProductWithSameName() {
        Category category = Category.builder()
                .categoryName("testCategory")
                .image("https://drive.google.com/file/d/1y03Ct0ABP1X8O6NFvK6FdqiMacYpLeTs/view?usp=drive_link")
                .products(new ArrayList<>())
                .build();

        categoryRepository.save(category);


        Product productTestOne = Product.builder()
                .name("TestProduct")
                .category(category)
                .description("testDescription")
                .price(BigDecimal.valueOf(100))
                .discountPrice(BigDecimal.valueOf(10))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .image("https://drive.google.com/file/first")
                .build();

        productRepository.save(productTestOne);

        Product productTestTwo = Product.builder()
                .name("TestProductSecond")
                .category(category)
                .description("testDescription")
                .price(BigDecimal.valueOf(100))
                .discountPrice(BigDecimal.valueOf(10))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .image("https://drive.google.com/file/first")
                .build();

        productRepository.save(productTestTwo);

        ProductUpdateDto requestDto = ProductUpdateDto.builder()
                .productName("TestProductSecond")
                .build();

        assertThrows(IllegalArgumentException.class, () -> productController.updateProduct(productTestOne.getId(),requestDto));
        assertEquals(2, productRepository.findAll().size());

    }

    @Test
    @WithMockUser(username = "testUser@email.com",
            roles = {"ADMIN", "MANAGER"})
    void testUpdateProductNameIfRoleAdminAndManagerAndNameIsTooShort() {
        Category category = Category.builder()
                .categoryName("testCategory")
                .image("https://drive.google.com/file/d/1y03Ct0ABP1X8O6NFvK6FdqiMacYpLeTs/view?usp=drive_link")
                .products(new ArrayList<>())
                .build();

        categoryRepository.save(category);

        Product productTestOne = Product.builder()
                .name("TestProduct")
                .category(category)
                .description("testDescription")
                .price(BigDecimal.valueOf(100))
                .discountPrice(BigDecimal.valueOf(10))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .image("https://drive.google.com/file/first")
                .build();

        productRepository.save(productTestOne);

        ProductUpdateDto requestDto = ProductUpdateDto.builder()
                .productName("te")
                .build();

        assertThrows(IllegalArgumentException.class, () -> productController.updateProduct(productTestOne.getId(),requestDto));
        assertEquals(1, productRepository.findAll().size());

    }

    @Test
    @WithMockUser(username = "testUser@email.com",
            roles = {"ADMIN", "MANAGER"})
    void testUpdateProductNameIfRoleAdminAndManagerAndNameIsTooLong() {
        Category category = Category.builder()
                .categoryName("testCategory")
                .image("https://drive.google.com/file/d/1y03Ct0ABP1X8O6NFvK6FdqiMacYpLeTs/view?usp=drive_link")
                .products(new ArrayList<>())
                .build();

        categoryRepository.save(category);

        Product productTestOne = Product.builder()
                .name("TestProduct")
                .category(category)
                .description("testDescription")
                .price(BigDecimal.valueOf(100))
                .discountPrice(BigDecimal.valueOf(10))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .image("https://drive.google.com/file/first")
                .build();

        productRepository.save(productTestOne);

        ProductUpdateDto requestDto = ProductUpdateDto.builder()
                .productName("TestProductTestProductTestProductTestProductTestProductTestProductTestProductTestProductTestProductTestProductTestProductTestProduct")
                .build();

        assertThrows(IllegalArgumentException.class, () -> productController.updateProduct(productTestOne.getId(),requestDto));
        assertEquals(1, productRepository.findAll().size());

    }

    @Test
    @WithMockUser(username = "testUser@email.com",
            roles = {"ADMIN", "MANAGER"})
    void testUpdateProductPriceIfRoleAdminAndManagerAndPriceIsZero() {
        Category category = Category.builder()
                .categoryName("testCategory")
                .image("https://drive.google.com/file/d/1y03Ct0ABP1X8O6NFvK6FdqiMacYpLeTs/view?usp=drive_link")
                .products(new ArrayList<>())
                .build();

        categoryRepository.save(category);

        Product productTestOne = Product.builder()
                .name("TestProduct")
                .category(category)
                .description("testDescription")
                .price(BigDecimal.valueOf(100))
                .discountPrice(BigDecimal.valueOf(10))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .image("https://drive.google.com/file/first")
                .build();

        productRepository.save(productTestOne);

        ProductUpdateDto requestDto = ProductUpdateDto.builder()
                .productPrice(BigDecimal.ZERO)
                .build();

        assertThrows(IllegalArgumentException.class, () -> productController.updateProduct(productTestOne.getId(),requestDto));
        assertEquals(1, productRepository.findAll().size());

    }

    @Test
    @WithMockUser(username = "testUser@email.com",
            roles = {"ADMIN", "MANAGER"})
    void testUpdateProductPriceIfRoleAdminAndManagerAndImageUrlNotValid() {
        Category category = Category.builder()
                .categoryName("testCategory")
                .image("https://drive.google.com/file/d/1y03Ct0ABP1X8O6NFvK6FdqiMacYpLeTs/view?usp=drive_link")
                .products(new ArrayList<>())
                .build();

        categoryRepository.save(category);

        Product productTestOne = Product.builder()
                .name("TestProduct")
                .category(category)
                .description("testDescription")
                .price(BigDecimal.valueOf(100))
                .discountPrice(BigDecimal.valueOf(10))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .image("https://drive.google.com/file/first")
                .build();

        productRepository.save(productTestOne);

        ProductUpdateDto requestDto = ProductUpdateDto.builder()
                .image("NOT VALID IMAGE")
                .build();

        assertThrows(UrlValidationException.class, () -> productController.updateProduct(productTestOne.getId(),requestDto));
        assertEquals(1, productRepository.findAll().size());

    }

    @Test
    @WithMockUser(username = "testUser@email.com", roles = "USER")
    void testUpdateProductIfRoleUser() {
        Category category = Category.builder()
                .categoryName("testCategory")
                .image("https://drive.google.com/file/d/1y03Ct0ABP1X8O6NFvK6FdqiMacYpLeTs/view?usp=drive_link")
                .products(new ArrayList<>())
                .build();

        categoryRepository.save(category);

        Product productTestOne = Product.builder()
                .name("TestProduct")
                .category(category)
                .description("testDescription")
                .price(BigDecimal.valueOf(100))
                .discountPrice(BigDecimal.valueOf(10))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .image("https://drive.google.com/file/first")
                .build();

        productRepository.save(productTestOne);

        ProductUpdateDto requestDto = ProductUpdateDto.builder()
                .productName("TestProduct")
                .build();

        Exception exception = assertThrows(AuthorizationDeniedException.class, () -> productController.updateProduct(productTestOne.getId(), requestDto));
        String messageException = "Access Denied";
        assertEquals(messageException, exception.getMessage());
        assertEquals(1, productRepository.findAll().size());
    }

    @Test
    void testUpdateProductIfUserIsNotRegistered() {
        Category category = Category.builder()
                .categoryName("testCategory")
                .image("https://drive.google.com/file/d/1y03Ct0ABP1X8O6NFvK6FdqiMacYpLeTs/view?usp=drive_link")
                .products(new ArrayList<>())
                .build();

        categoryRepository.save(category);

        Product productTestOne = Product.builder()
                .name("TestProduct")
                .category(category)
                .description("testDescription")
                .price(BigDecimal.valueOf(100))
                .discountPrice(BigDecimal.valueOf(10))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .image("https://drive.google.com/file/first")
                .build();

        productRepository.save(productTestOne);

        ProductUpdateDto requestDto = ProductUpdateDto.builder()
                .productName("TestProduct")
                .build();

        Exception exception = assertThrows(AuthenticationCredentialsNotFoundException.class, () -> productController.updateProduct(productTestOne.getId(), requestDto));
        String messageException = "An Authentication object was not found in the SecurityContext";
        assertEquals(messageException, exception.getMessage());
        assertEquals(1, productRepository.findAll().size());
    }

    @Test
    @WithMockUser(username = "testUser@email.com",
            roles = {"ADMIN", "MANAGER"})
    void testUpdateProductPriceIfRoleAdminAndManagerAndProductIdNotFound() {
        Category category = Category.builder()
                .categoryName("testCategory")
                .image("https://drive.google.com/file/d/1y03Ct0ABP1X8O6NFvK6FdqiMacYpLeTs/view?usp=drive_link")
                .products(new ArrayList<>())
                .build();

        categoryRepository.save(category);

        Product productTestOne = Product.builder()
                .name("TestProduct")
                .category(category)
                .description("testDescription")
                .price(BigDecimal.valueOf(100))
                .discountPrice(BigDecimal.valueOf(10))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .image("https://drive.google.com/file/first")
                .build();

        productRepository.save(productTestOne);

        ProductUpdateDto requestDto = ProductUpdateDto.builder()
                .image("https://drive.google.com/file/first")
                .build();

        assertThrows(IllegalArgumentException.class, () -> productController.updateProduct(10000, requestDto));
        assertEquals(1, productRepository.findAll().size());
    }

    @Test
    @WithMockUser(username = "testUser@email.com",
            roles = {"ADMIN", "MANAGER"})
    void testUpdateProductPriceIfRoleAdminAndManagerAndProductIdNull() {
        Category category = Category.builder()
                .categoryName("testCategory")
                .image("https://drive.google.com/file/d/1y03Ct0ABP1X8O6NFvK6FdqiMacYpLeTs/view?usp=drive_link")
                .products(new ArrayList<>())
                .build();

        categoryRepository.save(category);

        Product productTestOne = Product.builder()
                .name("TestProduct")
                .category(category)
                .description("testDescription")
                .price(BigDecimal.valueOf(100))
                .discountPrice(BigDecimal.valueOf(10))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .image("https://drive.google.com/file/first")
                .build();

        productRepository.save(productTestOne);

        ProductUpdateDto requestDto = ProductUpdateDto.builder()
                .image("https://drive.google.com/file/first")
                .build();

        assertThrows(InvalidDataAccessApiUsageException.class, () -> productController.updateProduct(null, requestDto));
        assertEquals(1, productRepository.findAll().size());
    }
}