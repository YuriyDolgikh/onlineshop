package org.onlineshop.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.onlineshop.dto.category.CategoryResponseDto;
import org.onlineshop.entity.Category;
import org.onlineshop.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.yml")
class CategoryServiceGetAllCategoriesTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryService categoryService;

    @AfterEach
    void dropDatabase() {
        categoryRepository.deleteAll();
    }

    @BeforeEach
    void setUp() {

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

    }

    @Test
    void testGetAllCategories() {
        List<CategoryResponseDto> result = categoryService.getAllCategories();
        CategoryResponseDto firstCategory = result.get(0);

        assertEquals(2, result.size());
        assertEquals(CategoryResponseDto.class, firstCategory.getClass());
    }
}