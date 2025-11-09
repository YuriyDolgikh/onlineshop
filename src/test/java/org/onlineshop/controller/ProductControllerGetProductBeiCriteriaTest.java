package org.onlineshop.controller;

import org.junit.jupiter.api.AfterEach;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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


        ResponseEntity<List<ProductResponseDto>> result = productController.getProductsByCriteria("price","100-300","asc");

        assertEquals(3, result.getBody().size());
        assertTrue(result.getBody().get(0).getProductPrice().compareTo(new BigDecimal("100")) == 0);
        assertTrue(result.getBody().get(1).getProductPrice().compareTo(new BigDecimal("200")) == 0);
        assertTrue(result.getBody().get(2).getProductPrice().compareTo(new BigDecimal("250")) == 0);

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


        ResponseEntity<List<ProductResponseDto>> result = productController.getProductsByCriteria("price","100-300","desc");

        assertEquals(3, result.getBody().size());
        assertTrue(result.getBody().get(2).getProductPrice().compareTo(new BigDecimal("100")) == 0);
        assertTrue(result.getBody().get(1).getProductPrice().compareTo(new BigDecimal("200")) == 0);
        assertTrue(result.getBody().get(0).getProductPrice().compareTo(new BigDecimal("250")) == 0);

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

        ResponseEntity<List<ProductResponseDto>> result = productController.getProductsByCriteria("discount"," ","desc");

        assertEquals(3, result.getBody().size());
        assertTrue(result.getBody().get(0).getProductDiscountPrice().compareTo(new BigDecimal("30")) == 0);
        assertTrue(result.getBody().get(1).getProductDiscountPrice().compareTo(new BigDecimal("20")) == 0);
        assertTrue(result.getBody().get(2).getProductDiscountPrice().compareTo(new BigDecimal("10")) == 0);

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

        ResponseEntity<List<ProductResponseDto>> result = productController.getProductsByCriteria("discount"," ","asc");

        assertEquals(3, result.getBody().size());
        assertTrue(result.getBody().get(2).getProductDiscountPrice().compareTo(new BigDecimal("30")) == 0);
        assertTrue(result.getBody().get(1).getProductDiscountPrice().compareTo(new BigDecimal("20")) == 0);
        assertTrue(result.getBody().get(0).getProductDiscountPrice().compareTo(new BigDecimal("10")) == 0);

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

        ResponseEntity<List<ProductResponseDto>> result = productController.getProductsByCriteria("name","te","desc");

        assertEquals(2, result.getBody().size());
        assertEquals(result.getBody().get(1).getProductName(),"testProductOne");
        assertEquals(result.getBody().get(0).getProductName(),"testProductTwo");

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

        ResponseEntity<List<ProductResponseDto>> result = productController.getProductsByCriteria("name","te","asc");

        assertEquals(2, result.getBody().size());
        assertEquals(result.getBody().get(0).getProductName(),"testProductOne");
        assertEquals(result.getBody().get(1).getProductName(),"testProductTwo");

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

        ResponseEntity<List<ProductResponseDto>> result = productController.getProductsByCriteria("category","testCategoryFirst","asc");

        assertEquals(2, result.getBody().size());
        assertEquals(result.getBody().get(0).getProductName(),"testProductOne");
        assertEquals(result.getBody().get(1).getProductName(),"testProductTwo");

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

        ResponseEntity<List<ProductResponseDto>> result = productController.getProductsByCriteria("createDate",dateNow,"asc");

        assertEquals(2, result.getBody().size());
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

        ResponseEntity<List<ProductResponseDto>> result = productController.getProductsByCriteria(" "," "," ");

        assertEquals(2, result.getBody().size());
    }



}