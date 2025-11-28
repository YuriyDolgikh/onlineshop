package org.onlineshop.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.onlineshop.dto.category.CategoryResponseDto;
import org.onlineshop.entity.Category;
import org.onlineshop.entity.User;
import org.onlineshop.repository.CategoryRepository;
import org.onlineshop.repository.ProductRepository;
import org.onlineshop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.yml")
class CategoryControllerGetAllCategoriesTest {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryController categoryController;
    @Autowired
    private MockMvc mockMvc;

    @AfterEach
    void dropDatabase() {
        categoryRepository.deleteAll();
        userRepository.deleteAll();
        productRepository.deleteAll();
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
    void testGetAllCategoriesIfRoleAdminAndManagerIfDateBaseNotEmpty() {
        Category categoryOne = Category.builder()
                .categoryName("testCategoryFirst")
                .image("https://drive.google.com/file/d/1y03Ct0ABP1X8O6NFvK6FdqiMacYpLeTs/view?usp=drive_link")
                .products(new ArrayList<>())
                .build();

        categoryRepository.save(categoryOne);

        Category categoryTwo = Category.builder()
                .categoryName("testCategorySecond")
                .image("https://drive.google.com/file/d/1y03Ct0ABP1X8O6NFvK6FdqiMacYpLeTs/view?usp=drive_link")
                .products(new ArrayList<>())
                .build();

        categoryRepository.save(categoryTwo);

        ResponseEntity<Page<CategoryResponseDto>> result = categoryController.getAllCategories(0, 2);

        Assertions.assertNotNull(result.getBody());
        assertEquals(2, result.getBody().getTotalElements());
    }

    @Test
    @WithMockUser(username = "testUser@email.com",
            roles = {"ADMIN", "MANAGER"})
    void testGetAllCategoriesIfRoleAdminAndManagerIfDateBaseEmpty() {
        ResponseEntity<Page<CategoryResponseDto>> result = categoryController.getAllCategories(0, 2);
        Assertions.assertNotNull(result.getBody());
        assertEquals(0, result.getBody().getTotalElements());
    }

    @Test
    @WithMockUser(username = "testUser@email.com",
            roles = "USER")
    void testGetAllCategoriesIfRoleUser() throws Exception {
        mockMvc.perform(get("/v1/categories/")).andExpect(status().isForbidden());
    }

    @Test
    void testGetAllCategoriesIfUserNotRegistered() throws Exception {
        mockMvc.perform(get("/v1/categories/")).andExpect(status().isUnauthorized());
    }

}