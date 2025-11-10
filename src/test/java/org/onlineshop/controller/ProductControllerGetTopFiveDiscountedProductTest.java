package org.onlineshop.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.onlineshop.dto.product.ProductResponseForUserDto;
import org.onlineshop.entity.Category;
import org.onlineshop.entity.Product;
import org.onlineshop.entity.User;
import org.onlineshop.exception.NotFoundException;
import org.onlineshop.repository.CategoryRepository;
import org.onlineshop.repository.ProductRepository;
import org.onlineshop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.yml")
class ProductControllerGetTopFiveDiscountedProductTest {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductController productController;

    @Autowired
    private MockMvc mockMvc;

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
    void getTopFiveDiscountedProductsOfTheDayIfRoleUserAdminManagerAndDateBaseNotEmpty() {
        Category category = Category.builder()
                .categoryName("testCategory")
                .image("https://drive.google.com/file/d/1y03Ct0ABP1X8O6NFvK6FdqiMacYpLeTs/view?usp=drive_link")
                .products(new ArrayList<>())
                .build();

        categoryRepository.save(category);

        Product productTestOne = Product.builder()
                .name("TestProductOne")
                .category(category)
                .description("testDescription")
                .price(BigDecimal.valueOf(100))
                .discountPrice(BigDecimal.valueOf(90))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .image("https://drive.google.com/file/first")
                .build();

        productRepository.save(productTestOne);

        Product productTestTwo = Product.builder()
                .name("TestProductTwo")
                .category(category)
                .description("testDescription")
                .price(BigDecimal.valueOf(100))
                .discountPrice(BigDecimal.valueOf(40))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .image("https://drive.google.com/file/two")
                .build();

        productRepository.save(productTestTwo);

        Product productTestThree = Product.builder()
                .name("TestProductThree")
                .category(category)
                .description("testDescription")
                .price(BigDecimal.valueOf(100))
                .discountPrice(BigDecimal.valueOf(50))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .image("https://drive.google.com/file/three")
                .build();

        productRepository.save(productTestThree);

        Product productTestFour = Product.builder()
                .name("TestProductFour")
                .category(category)
                .description("testDescription")
                .price(BigDecimal.valueOf(100))
                .discountPrice(BigDecimal.valueOf(70))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .image("https://drive.google.com/file/Four")
                .build();

        productRepository.save(productTestFour);

        Product productTestFive = Product.builder()
                .name("TestProductFive")
                .category(category)
                .description("testDescription")
                .price(BigDecimal.valueOf(100))
                .discountPrice(BigDecimal.valueOf(80))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .image("https://drive.google.com/file/five")
                .build();

        productRepository.save(productTestFive);

        Product productTestSix = Product.builder()
                .name("TestProductSix")
                .category(category)
                .description("testDescription")
                .price(BigDecimal.valueOf(100))
                .discountPrice(BigDecimal.valueOf(0))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .image("https://drive.google.com/file/six")
                .build();

        productRepository.save(productTestSix);

        ResponseEntity<List<ProductResponseForUserDto>> result = productController.getTopFiveDiscountedProductsOfTheDay();

        assertEquals(5, result.getBody().size());
        assertTrue(result.getBody().get(0).getProductDiscountPrice().compareTo(new BigDecimal("90")) == 0);
        assertTrue(result.getBody().get(1).getProductDiscountPrice().compareTo(new BigDecimal("80")) == 0);
        assertTrue(result.getBody().get(2).getProductDiscountPrice().compareTo(new BigDecimal("70")) == 0);
        assertTrue(result.getBody().get(3).getProductDiscountPrice().compareTo(new BigDecimal("50")) == 0);
        assertTrue(result.getBody().get(4).getProductDiscountPrice().compareTo(new BigDecimal("40")) == 0);

    }

    @Test
    @WithMockUser(username = "testUser@email.com",
            roles = {"ADMIN", "MANAGER", "USER"})
    void getTopFiveDiscountedProductsOfTheDayIfRoleUserAdminManagerAndDateBaseEmpty() {
        assertThrows(NotFoundException.class, () -> productController.getTopFiveDiscountedProductsOfTheDay());
    }

    @Test
    void getTopFiveDiscountedProductsOfTheDayIfUserNotRegistered() throws Exception {
        Category category = Category.builder()
                .categoryName("testCategory")
                .image("https://drive.google.com/file/d/1y03Ct0ABP1X8O6NFvK6FdqiMacYpLeTs/view?usp=drive_link")
                .products(new ArrayList<>())
                .build();

        categoryRepository.save(category);

        Product productTestOne = Product.builder()
                .name("TestProductOne")
                .category(category)
                .description("testDescription")
                .price(BigDecimal.valueOf(100))
                .discountPrice(BigDecimal.valueOf(90))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .image("https://drive.google.com/file/first")
                .build();

        productRepository.save(productTestOne);

        Product productTestTwo = Product.builder()
                .name("TestProductTwo")
                .category(category)
                .description("testDescription")
                .price(BigDecimal.valueOf(100))
                .discountPrice(BigDecimal.valueOf(40))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .image("https://drive.google.com/file/two")
                .build();

        productRepository.save(productTestTwo);

        Product productTestThree = Product.builder()
                .name("TestProductThree")
                .category(category)
                .description("testDescription")
                .price(BigDecimal.valueOf(100))
                .discountPrice(BigDecimal.valueOf(50))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .image("https://drive.google.com/file/three")
                .build();

        productRepository.save(productTestThree);

        Product productTestFour = Product.builder()
                .name("TestProductFour")
                .category(category)
                .description("testDescription")
                .price(BigDecimal.valueOf(100))
                .discountPrice(BigDecimal.valueOf(70))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .image("https://drive.google.com/file/Four")
                .build();

        productRepository.save(productTestFour);

        Product productTestFive = Product.builder()
                .name("TestProductFive")
                .category(category)
                .description("testDescription")
                .price(BigDecimal.valueOf(100))
                .discountPrice(BigDecimal.valueOf(80))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .image("https://drive.google.com/file/five")
                .build();

        productRepository.save(productTestFive);

        Product productTestSix = Product.builder()
                .name("TestProductSix")
                .category(category)
                .description("testDescription")
                .price(BigDecimal.valueOf(100))
                .discountPrice(BigDecimal.valueOf(0))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .image("https://drive.google.com/file/six")
                .build();

        productRepository.save(productTestSix);

        mockMvc.perform(get("/v1/products/getTopFiveProducts")).andExpect(status().isUnauthorized());
    }

}