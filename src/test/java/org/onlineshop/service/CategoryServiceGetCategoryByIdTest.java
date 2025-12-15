package org.onlineshop.service;

import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.onlineshop.entity.Category;
import org.onlineshop.exception.BadRequestException;
import org.onlineshop.exception.NotFoundException;
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
class CategoryServiceGetCategoryByIdTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryService categoryService;

    @AfterEach
    void dropDatabase() {
        categoryRepository.deleteAll();
    }

    @Test
    void testGetCategoryByIdIfOk() {
        Category categoryOne = Category.builder()
                .categoryName("testCategoryOne")
                .image("https://drive.google.com/one")
                .products(new ArrayList<>())
                .build();

        categoryRepository.save(categoryOne);

        Category categoryTwo = Category.builder()
                .categoryName("testCategoryTwo")
                .image("https://drive.google.com/two")
                .products(new ArrayList<>())
                .build();

        categoryRepository.save(categoryTwo);


        Category result = categoryService.getCategoryById(categoryOne.getCategoryId());
        assertEquals(result.getCategoryName(), categoryOne.getCategoryName());
    }

    @Test
    void testGetCategoryByIdIfIdNotFound() {
        Exception exception = assertThrows(NotFoundException.class, () -> categoryService.getCategoryById(100000));
        String messageException = "Category with id: " + 100000 + " not found";
        assertEquals(messageException, exception.getMessage());
    }
}