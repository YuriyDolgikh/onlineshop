package org.onlineshop.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.onlineshop.dto.category.CategoryRequestDto;
import org.onlineshop.dto.category.CategoryResponseDto;
import org.onlineshop.entity.Category;
import org.onlineshop.entity.User;
import org.onlineshop.exception.BadRequestException;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.yml")
class CategoryControllerAddNewCategoryTest {
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
        productRepository.deleteAll();
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
                .categoryName("testCategoryOther")
                .image("https://drive.google.com/file/d/1y03Ct0ABP1X8O6NFvK6FdqiMacYpLeTs/view?usp=drive_link")
                .products(new ArrayList<>())
                .build();

        categoryRepository.save(category);

    }

    @Test
    @WithMockUser(username = "testUser@email.com",
            roles = {"ADMIN", "MANAGER"})
    void testAddCategoryIfRoleAdminAndManagerAndIfOk() {
        CategoryRequestDto categoryRequestDto = CategoryRequestDto.builder()
                .categoryName("TestCategory")
                .image("https://drive.google.com/file/two")
                .build();

        ResponseEntity<CategoryResponseDto> category = categoryController.addCategory(categoryRequestDto);
        assertNotNull(category);
        assertNotNull(category.getBody());
        assertEquals(categoryRequestDto.getCategoryName(), category.getBody().getCategoryName());
        assertEquals(categoryRequestDto.getImage(), category.getBody().getImage());
        assertEquals(2, categoryRepository.findAll().size());
    }

    @Test
    @WithMockUser(username = "testUser@email.com", roles = "USER")
    void testAddCategoryIfRoleUser() throws Exception {
        mockMvc.perform(post("/v1/categories")).andExpect(status().isForbidden());
    }

    @Test
    void testAddCategoryIfUserNotRegistered() throws Exception {
        mockMvc.perform(post("/v1/categories")).andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "testUser@email.com",
            roles = {"ADMIN", "MANAGER"})
    void testAddCategoryIfRoleAdminAndManagerAndNameTooLong() {
        CategoryRequestDto categoryRequestDto = CategoryRequestDto.builder()
                .categoryName("TestCategoryTestCategoryTestCategoryTestCategoryTestCategoryTestCategory")
                .image("https://drive.google.com/file/two")
                .build();

        assertThrows(IllegalArgumentException.class, () -> categoryController.addCategory(categoryRequestDto));
        assertEquals(1, categoryRepository.findAll().size());
    }

    @Test
    @WithMockUser(username = "testUser@email.com",
            roles = {"ADMIN", "MANAGER"})
    void testAddCategoryIfRoleAdminAndManagerAndNameTooShort() throws Exception {
        CategoryRequestDto categoryRequestDto = CategoryRequestDto.builder()
                .categoryName("Te")
                .image("https://drive.google.com/file/two")
                .build();

        mockMvc.perform(post("/v1/categories")).andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "testUser@email.com",
            roles = {"ADMIN", "MANAGER"})
    void testAddCategoryIfRoleAdminAndManagerAndNameCategoryIsAlreadyExist() {
        CategoryRequestDto categoryRequestDto = CategoryRequestDto.builder()
                .categoryName("testCategoryOther")
                .image("https://drive.google.com/file/two")
                .build();

        assertThrows(BadRequestException.class, () -> categoryController.addCategory(categoryRequestDto));
        assertEquals(1, categoryRepository.findAll().size());

    }
}