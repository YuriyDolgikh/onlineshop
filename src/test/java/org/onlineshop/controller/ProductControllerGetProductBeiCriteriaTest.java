package org.onlineshop.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.onlineshop.dto.product.ProductResponseDto;
import org.onlineshop.entity.Category;
import org.onlineshop.entity.Product;
import org.onlineshop.entity.User;
import org.onlineshop.repository.CategoryRepository;
import org.onlineshop.repository.ProductRepository;
import org.onlineshop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.yml")
class ProductControllerGetProductBeiCriteriaTest {
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
            roles = {"ADMIN", "MANAGER", "USER"})
    void testGetProductsByCriteriaPriceRangeAscIfRoleAdminUserManagerIfOk() {
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

        Product productTestTwo = Product.builder()
                .name("testProductTwo")
                .category(categoryFirst)
                .description("testDescription")
                .price(BigDecimal.valueOf(200))
                .discountPrice(BigDecimal.valueOf(10))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .image("https://drive.google.com/file/one")
                .build();

        productRepository.save(productTestTwo);

        Product productTestThree = Product.builder()
                .name("testProductThree")
                .category(categoryFirst)
                .description("testDescription")
                .price(BigDecimal.valueOf(250))
                .discountPrice(BigDecimal.valueOf(10))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .image("https://drive.google.com/file/three")
                .build();

        productRepository.save(productTestThree);

        Product productTestFour = Product.builder()
                .name("testProductFour")
                .category(categoryFirst)
                .description("testDescription")
                .price(BigDecimal.valueOf(400))
                .discountPrice(BigDecimal.valueOf(10))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .image("https://drive.google.com/file/four")
                .build();

        productRepository.save(productTestFour);


        ResponseEntity<Page<ProductResponseDto>> resultPage = productController.getProductsByCriteria("price", "100-300", "asc", 0, 5);

        Assertions.assertNotNull(resultPage.getBody());
        assertEquals(3, resultPage.getBody().getTotalElements());

        assertEquals(0, resultPage.getBody().getContent().get(0).getProductPrice().compareTo(new BigDecimal("100")));
        assertEquals(0, resultPage.getBody().getContent().get(1).getProductPrice().compareTo(new BigDecimal("200")));
        assertEquals(0, resultPage.getBody().getContent().get(2).getProductPrice().compareTo(new BigDecimal("250")));

    }

    @Test
    @WithMockUser(username = "testUser@email.com",
            roles = {"ADMIN", "MANAGER", "USER"})
    void testGetProductsByCriteriaPriceRangeDescIfRoleAdminUserManagerIfOk() {
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

        Product productTestTwo = Product.builder()
                .name("testProductTwo")
                .category(categoryFirst)
                .description("testDescription")
                .price(BigDecimal.valueOf(200))
                .discountPrice(BigDecimal.valueOf(10))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .image("https://drive.google.com/file/one")
                .build();

        productRepository.save(productTestTwo);

        Product productTestThree = Product.builder()
                .name("testProductThree")
                .category(categoryFirst)
                .description("testDescription")
                .price(BigDecimal.valueOf(250))
                .discountPrice(BigDecimal.valueOf(10))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .image("https://drive.google.com/file/three")
                .build();

        productRepository.save(productTestThree);

        Product productTestFour = Product.builder()
                .name("testProductFour")
                .category(categoryFirst)
                .description("testDescription")
                .price(BigDecimal.valueOf(400))
                .discountPrice(BigDecimal.valueOf(10))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .image("https://drive.google.com/file/four")
                .build();

        productRepository.save(productTestFour);


        ResponseEntity<Page<ProductResponseDto>> resultPage = productController.getProductsByCriteria("price", "100-300", "desc", 0, 5);

        Assertions.assertNotNull(resultPage.getBody());
        assertEquals(3, resultPage.getBody().getTotalElements());

        assertEquals(0, resultPage.getBody().getContent().get(0).getProductPrice().compareTo(new BigDecimal("250")));
        assertEquals(0, resultPage.getBody().getContent().get(1).getProductPrice().compareTo(new BigDecimal("200")));
        assertEquals(0, resultPage.getBody().getContent().get(2).getProductPrice().compareTo(new BigDecimal("100")));

    }

    @Test
    @WithMockUser(username = "testUser@email.com",
            roles = {"ADMIN", "MANAGER", "USER"})
    void testGetProductsByCriteriaPriceDiscountDescIfRoleAdminUserManagerIfOk() {
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

        Product productTestTwo = Product.builder()
                .name("testProductTwo")
                .category(categoryFirst)
                .description("testDescription")
                .price(BigDecimal.valueOf(200))
                .discountPrice(BigDecimal.valueOf(20))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .image("https://drive.google.com/file/one")
                .build();

        productRepository.save(productTestTwo);

        Product productTestThree = Product.builder()
                .name("testProductThree")
                .category(categoryFirst)
                .description("testDescription")
                .price(BigDecimal.valueOf(250))
                .discountPrice(BigDecimal.valueOf(30))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .image("https://drive.google.com/file/three")
                .build();

        productRepository.save(productTestThree);

        ResponseEntity<Page<ProductResponseDto>> resultPage = productController.getProductsByCriteria("discount", " ", "desc", 0, 5);

        Assertions.assertNotNull(resultPage.getBody());
        assertEquals(3, resultPage.getBody().getTotalElements());

        assertEquals(0, resultPage.getBody().getContent().get(0).getProductDiscountPrice().compareTo(new BigDecimal("30")));
        assertEquals(0, resultPage.getBody().getContent().get(1).getProductDiscountPrice().compareTo(new BigDecimal("20")));
        assertEquals(0, resultPage.getBody().getContent().get(2).getProductDiscountPrice().compareTo(new BigDecimal("10")));

    }

    @Test
    @WithMockUser(username = "testUser@email.com",
            roles = {"ADMIN", "MANAGER", "USER"})
    void testGetProductsByCriteriaPriceDiscountAscIfRoleAdminUserManagerIfOk() {
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

        Product productTestTwo = Product.builder()
                .name("testProductTwo")
                .category(categoryFirst)
                .description("testDescription")
                .price(BigDecimal.valueOf(200))
                .discountPrice(BigDecimal.valueOf(20))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .image("https://drive.google.com/file/one")
                .build();

        productRepository.save(productTestTwo);

        Product productTestThree = Product.builder()
                .name("testProductThree")
                .category(categoryFirst)
                .description("testDescription")
                .price(BigDecimal.valueOf(250))
                .discountPrice(BigDecimal.valueOf(30))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .image("https://drive.google.com/file/three")
                .build();

        productRepository.save(productTestThree);

        ResponseEntity<Page<ProductResponseDto>> resultPage = productController.getProductsByCriteria("discount", " ", "asc", 0, 5);

        Assertions.assertNotNull(resultPage.getBody());
        assertEquals(3, resultPage.getBody().getTotalElements());

        assertEquals(0, resultPage.getBody().getContent().get(2).getProductDiscountPrice().compareTo(new BigDecimal("30")));
        assertEquals(0, resultPage.getBody().getContent().get(1).getProductDiscountPrice().compareTo(new BigDecimal("20")));
        assertEquals(0, resultPage.getBody().getContent().get(0).getProductDiscountPrice().compareTo(new BigDecimal("10")));
    }

    @Test
    @WithMockUser(username = "testUser@email.com",
            roles = {"ADMIN", "MANAGER", "USER"})
    void testGetProductsByCriteriaNameDescIfRoleAdminUserManagerIfOk() {
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

        Product productTestTwo = Product.builder()
                .name("testProductTwo")
                .category(categoryFirst)
                .description("testDescription")
                .price(BigDecimal.valueOf(200))
                .discountPrice(BigDecimal.valueOf(20))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .image("https://drive.google.com/file/one")
                .build();

        productRepository.save(productTestTwo);

        Product productTestThree = Product.builder()
                .name("ProductThree")
                .category(categoryFirst)
                .description("testDescription")
                .price(BigDecimal.valueOf(250))
                .discountPrice(BigDecimal.valueOf(30))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .image("https://drive.google.com/file/three")
                .build();

        productRepository.save(productTestThree);

        ResponseEntity<Page<ProductResponseDto>> resultPage = productController.getProductsByCriteria("name", "te", "desc", 0, 5);

        Assertions.assertNotNull(resultPage.getBody());
        assertEquals(2, resultPage.getBody().getTotalElements());

        assertEquals("testProductOne", resultPage.getBody().getContent().get(1).getProductName());
        assertEquals("testProductTwo", resultPage.getBody().getContent().get(0).getProductName());

    }

    @Test
    @WithMockUser(username = "testUser@email.com",
            roles = {"ADMIN", "MANAGER", "USER"})
    void testGetProductsByCriteriaNameAscIfRoleAdminUserManagerIfOk() {
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

        Product productTestTwo = Product.builder()
                .name("testProductTwo")
                .category(categoryFirst)
                .description("testDescription")
                .price(BigDecimal.valueOf(200))
                .discountPrice(BigDecimal.valueOf(20))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .image("https://drive.google.com/file/one")
                .build();

        productRepository.save(productTestTwo);

        Product productTestThree = Product.builder()
                .name("ProductThree")
                .category(categoryFirst)
                .description("testDescription")
                .price(BigDecimal.valueOf(250))
                .discountPrice(BigDecimal.valueOf(30))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .image("https://drive.google.com/file/three")
                .build();

        productRepository.save(productTestThree);

        ResponseEntity<Page<ProductResponseDto>> resultPage = productController.getProductsByCriteria("name", "te", "asc", 0, 5);

        Assertions.assertNotNull(resultPage.getBody());
        assertEquals(2, resultPage.getBody().getTotalElements());

        assertEquals("testProductOne", resultPage.getBody().getContent().get(0).getProductName());
        assertEquals("testProductTwo", resultPage.getBody().getContent().get(1).getProductName());

    }

    @Test
    @WithMockUser(username = "testUser@email.com",
            roles = {"ADMIN", "MANAGER", "USER"})
    void testGetProductsByCategoryIfRoleAdminUserManagerIfOk() {
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

        Product productTestTwo = Product.builder()
                .name("testProductTwo")
                .category(categoryFirst)
                .description("testDescription")
                .price(BigDecimal.valueOf(200))
                .discountPrice(BigDecimal.valueOf(20))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .image("https://drive.google.com/file/one")
                .build();

        productRepository.save(productTestTwo);

        Product productTestThree = Product.builder()
                .name("ProductThree")
                .category(categorySecond)
                .description("testDescription")
                .price(BigDecimal.valueOf(250))
                .discountPrice(BigDecimal.valueOf(30))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .image("https://drive.google.com/file/three")
                .build();

        productRepository.save(productTestThree);

        ResponseEntity<Page<ProductResponseDto>> resultPage = productController.getProductsByCriteria("category", "testCategoryFirst", "asc", 0, 5);

        Assertions.assertNotNull(resultPage.getBody());
        assertEquals(2, resultPage.getBody().getTotalElements());

        assertEquals("testProductOne", resultPage.getBody().getContent().get(0).getProductName());
        assertEquals("testProductTwo", resultPage.getBody().getContent().get(1).getProductName());
    }

    @Test
    @WithMockUser(username = "testUser@email.com",
            roles = {"ADMIN", "MANAGER", "USER"})
    void testGetProductsByCreateDateIfRoleAdminUserManagerIfOk() {
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

        Product productTestTwo = Product.builder()
                .name("testProductTwo")
                .category(categoryFirst)
                .description("testDescription")
                .price(BigDecimal.valueOf(200))
                .discountPrice(BigDecimal.valueOf(20))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .image("https://drive.google.com/file/one")
                .build();

        productRepository.save(productTestTwo);

        LocalDateTime now = LocalDateTime.now();
        String dateNow = DateTimeFormatter.ofPattern("yyyy-MM-dd").format(now);

        ResponseEntity<Page<ProductResponseDto>> resultPage = productController.getProductsByCriteria("createDate", dateNow, "asc", 0, 5);

        Assertions.assertNotNull(resultPage.getBody());
        assertEquals(2, resultPage.getBody().getTotalElements());
    }

    @Test
    @WithMockUser(username = "testUser@email.com",
            roles = {"ADMIN", "MANAGER", "USER"})
    void testGetProductsByGetAllProductIfRoleAdminUserManagerIfOk() {
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

        Product productTestTwo = Product.builder()
                .name("testProductTwo")
                .category(categoryFirst)
                .description("testDescription")
                .price(BigDecimal.valueOf(200))
                .discountPrice(BigDecimal.valueOf(20))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .image("https://drive.google.com/file/one")
                .build();

        productRepository.save(productTestTwo);

        ResponseEntity<Page<ProductResponseDto>> result = productController.getProductsByCriteria(" ", " ", "asc", 0, 5);

        Assertions.assertNotNull(result.getBody());
        assertEquals(2, result.getBody().getTotalElements());
    }


}