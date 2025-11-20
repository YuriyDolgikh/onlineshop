package org.onlineshop.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.onlineshop.dto.category.CategoryResponseDto;
import org.onlineshop.dto.category.CategoryUpdateDto;
import org.onlineshop.entity.Category;
import org.onlineshop.entity.User;
import org.onlineshop.exception.UrlValidationException;
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

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.yml")
class CategoryControllerUpdateCategoryTest {
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
                .categoryName("testCategoryForOtherTest")
                .image("https://drive.google.com/file/d/1y03Ct0ABP1X8O6NFvK6FdqiMacYpLeTs/view?usp=drive_link")
                .products(new ArrayList<>())
                .build();

        categoryRepository.save(category);

    }

    @Test
    @WithMockUser(username = "testUser@email.com",
            roles = {"ADMIN", "MANAGER"})
    void testUpdateCategoryNameIfRoleAdminAndManagerAndIfOk() {
        Category category = Category.builder()
                .categoryName("testCategoryFirst")
                .image("https://drive.google.com/file/d/1y03Ct0ABP1X8O6NFvK6FdqiMacYpLeTs/view?usp=drive_link")
                .products(new ArrayList<>())
                .build();

        categoryRepository.save(category);

        CategoryUpdateDto categoryUpdateDto = CategoryUpdateDto.builder()
                .categoryName("NewNameCategory")
                .build();

        ResponseEntity<CategoryResponseDto> updateCategory = categoryController.updateCategory(category.getCategoryId(), categoryUpdateDto);
        assertNotNull(updateCategory);
        assertEquals(categoryUpdateDto.getCategoryName(), updateCategory.getBody().getCategoryName());
        assertEquals(2, categoryRepository.findAll().size());
    }

    @Test
    @WithMockUser(username = "testUser@email.com",
            roles = {"ADMIN", "MANAGER"})
    void testUpdateCategoryImageIfRoleAdminAndManagerAndIfOk() {
        Category category = Category.builder()
                .categoryName("testCategoryFirst")
                .image("https://drive.google.com/file/d/1y03Ct0ABP1X8O6NFvK6FdqiMacYpLeTs/view?usp=drive_link")
                .products(new ArrayList<>())
                .build();

        categoryRepository.save(category);

        CategoryUpdateDto categoryUpdateDto = CategoryUpdateDto.builder()
                .image("https://drive.google.com/newImage")
                .build();

        ResponseEntity<CategoryResponseDto> updateCategory = categoryController.updateCategory(category.getCategoryId(), categoryUpdateDto);
        assertNotNull(updateCategory);
        assertEquals(categoryUpdateDto.getImage(), updateCategory.getBody().getImage());
        assertEquals(2, categoryRepository.findAll().size());
    }

    @Test
    @WithMockUser(username = "testUser@email.com",
            roles = {"ADMIN", "MANAGER"})
    void testUpdateCategoryNameIfRoleAdminAndManagerAndIfNameTooShort() {
        Category category = Category.builder()
                .categoryName("testCategoryFirst")
                .image("https://drive.google.com/file/d/1y03Ct0ABP1X8O6NFvK6FdqiMacYpLeTs/view?usp=drive_link")
                .products(new ArrayList<>())
                .build();

        categoryRepository.save(category);

        CategoryUpdateDto categoryUpdateDto = CategoryUpdateDto.builder()
                .categoryName("Ne")
                .build();

        assertThrows(IllegalArgumentException.class, () -> categoryController.updateCategory(category.getCategoryId(), categoryUpdateDto));
        assertEquals(2, categoryRepository.findAll().size());
    }

    @Test
    @WithMockUser(username = "testUser@email.com",
            roles = {"ADMIN", "MANAGER"})
    void testUpdateCategoryNameIfRoleAdminAndManagerAndIfNameTooLong() {
        Category category = Category.builder()
                .categoryName("testCategoryFirst")
                .image("https://drive.google.com/file/d/1y03Ct0ABP1X8O6NFvK6FdqiMacYpLeTs/view?usp=drive_link")
                .products(new ArrayList<>())
                .build();

        categoryRepository.save(category);

        CategoryUpdateDto categoryUpdateDto = CategoryUpdateDto.builder()
                .categoryName("testCategoryFirsttestCategoryFirsttestCategoryFirsttestCategoryFirsttestCategoryFirsttestCategoryFirsttestCategoryFirsttestCategoryFirsttestCategoryFirsttestCategoryFirsttestCategoryFirst")
                .build();

        assertThrows(IllegalArgumentException.class, () -> categoryController.updateCategory(category.getCategoryId(), categoryUpdateDto));
        assertEquals(2, categoryRepository.findAll().size());
    }

    @Test
    @WithMockUser(username = "testUser@email.com",
            roles = {"ADMIN", "MANAGER"})
    void testUpdateCategoryNameIfRoleAdminAndManagerAndIfImageUrlNotFound() {
        Category category = Category.builder()
                .categoryName("testCategoryFirst")
                .image("https://drive.google.com/file/d/1y03Ct0ABP1X8O6NFvK6FdqiMacYpLeTs/view?usp=drive_link")
                .products(new ArrayList<>())
                .build();

        categoryRepository.save(category);

        CategoryUpdateDto categoryUpdateDto = CategoryUpdateDto.builder()
                .image("Not Valid Image")
                .build();

        assertThrows(UrlValidationException.class, () -> categoryController.updateCategory(category.getCategoryId(), categoryUpdateDto));
        assertEquals(2, categoryRepository.findAll().size());
    }

    @Test
    @WithMockUser(username = "testUser@email.com", roles = "USER")
    void testUpdateCategoryNameIfRoleUser() throws Exception {
        Category category = Category.builder()
                .categoryName("testCategoryFirst")
                .image("https://drive.google.com/file/d/1y03Ct0ABP1X8O6NFvK6FdqiMacYpLeTs/view?usp=drive_link")
                .products(new ArrayList<>())
                .build();

        categoryRepository.save(category);

        mockMvc.perform(put("/v1/categories/" + category.getCategoryId())).andExpect(status().isForbidden());
    }

    @Test
    void testUpdateCategoryNameIfUserNotRegistered() throws Exception {
        Category category = Category.builder()
                .categoryName("testCategoryFirst")
                .image("https://drive.google.com/file/d/1y03Ct0ABP1X8O6NFvK6FdqiMacYpLeTs/view?usp=drive_link")
                .products(new ArrayList<>())
                .build();

        categoryRepository.save(category);

        mockMvc.perform(put("/v1/categories/" + category.getCategoryId())).andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "testUser@email.com",
            roles = {"ADMIN", "MANAGER"})
    void testUpdateCategoryNameIfRoleAdminAndManagerAndIfCategoryNameAlreadyExists() {
        Category category = Category.builder()
                .categoryName("testCategoryFirst")
                .image("https://drive.google.com/file/d/1y03Ct0ABP1X8O6NFvK6FdqiMacYpLeTs/view?usp=drive_link")
                .products(new ArrayList<>())
                .build();

        categoryRepository.save(category);

        CategoryUpdateDto categoryUpdateDto = CategoryUpdateDto.builder()
                .categoryName("testCategoryForOtherTest")
                .build();

        assertThrows(IllegalArgumentException.class, () -> categoryController.updateCategory(category.getCategoryId(), categoryUpdateDto));
        assertEquals(2, categoryRepository.findAll().size());
    }
}