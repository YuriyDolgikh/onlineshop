package org.onlineshop.service;

import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.onlineshop.entity.Category;
import org.onlineshop.exception.BadRequestException;
import org.onlineshop.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.yml")
class CategoryServiceDeleteCategoryTest {
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryService categoryService;

    @AfterEach
    void dropDatabase() {
        categoryRepository.deleteAll();
    }

    @Test
    void testDeleteCategoryIfOk() {

        Category categoryTest = Category.builder()
                .categoryName("testCategoryFirst")
                .image("https://drive.google.com/file/first")
                .products(new ArrayList<>())
                .build();

        categoryRepository.save(categoryTest);

       categoryService.deleteCategory(categoryTest.getCategoryId());

        assertFalse(categoryRepository.findById(categoryTest.getCategoryId()).isPresent());
        assertEquals(0, categoryRepository.findAll().size());
    }

    @Test
    void testDeleteCategoryIfProductNotFound() {
        Exception exception = assertThrows(BadRequestException.class, () -> categoryService.deleteCategory(100000));
        String messageException = "Category with id: 100000 not found";
        assertEquals(messageException, exception.getMessage());

    }

    @Test
    void testDeleteCategoryIfProductIdNull() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> categoryService.deleteCategory(null));
        String messageException = "Category id must be provided";
        assertEquals(messageException, exception.getMessage());

    }
}