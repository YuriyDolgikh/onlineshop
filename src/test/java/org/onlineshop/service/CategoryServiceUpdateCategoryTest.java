package org.onlineshop.service;

import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.onlineshop.dto.category.CategoryResponseDto;
import org.onlineshop.dto.category.CategoryUpdateDto;
import org.onlineshop.entity.Category;
import org.onlineshop.exception.BadRequestException;
import org.onlineshop.exception.NotFoundException;
import org.onlineshop.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.yml")
class CategoryServiceUpdateCategoryTest {
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private ValidatorFactory validatorFactory;

    @AfterEach
    void dropDatabase() {
        categoryRepository.deleteAll();
    }

    @Test
    void testUpdateCategoryNameIfOk() {
        Category category = Category.builder()
                .categoryName("testCategory")
                .image("https://drive.google.com/file/d/1y03Ct0ABP1X8O6NFvK6FdqiMacYpLeTs/view?usp=drive_link")
                .products(new ArrayList<>())
                .build();

        categoryRepository.save(category);

        CategoryUpdateDto categoryUpdateDto = CategoryUpdateDto.builder()
                .categoryName("NewCategory")
                .build();

        CategoryResponseDto categoryForTest = categoryService.updateCategory(category.getCategoryId(), categoryUpdateDto);

        assertNotNull(categoryForTest);
        assertEquals(categoryUpdateDto.getCategoryName(), categoryForTest.getCategoryName());
        assertEquals("https://drive.google.com/file/d/1y03Ct0ABP1X8O6NFvK6FdqiMacYpLeTs/view?usp=drive_link", categoryForTest.getImage());
        assertEquals(1, categoryRepository.findAll().size());
    }

    @Test
    void testUpdateCategoryImageIfOk() {
        Category category = Category.builder()
                .categoryName("testCategory")
                .image("https://drive.google.com/file/d/1y03Ct0ABP1X8O6NFvK6FdqiMacYpLeTs/view?usp=drive_link")
                .products(new ArrayList<>())
                .build();

        categoryRepository.save(category);

        CategoryUpdateDto categoryUpdateDto = CategoryUpdateDto.builder()
                .image("https://drive.google.com/update")
                .build();

        CategoryResponseDto categoryForTest = categoryService.updateCategory(category.getCategoryId(), categoryUpdateDto);

        assertNotNull(categoryForTest);
        assertEquals(categoryUpdateDto.getImage(), categoryForTest.getImage());
        assertEquals("testCategory", categoryForTest.getCategoryName());
        assertEquals(1, categoryRepository.findAll().size());
    }

    @Test
    void testUpdateCategoryNameIfNameAlreadyExists() {
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

        CategoryUpdateDto categoryUpdateDto = CategoryUpdateDto.builder()
                .categoryName("testCategorySecond")
                .build();

        assertThrows(DataIntegrityViolationException.class, () -> categoryService.updateCategory(categoryOne.getCategoryId(), categoryUpdateDto));
    }

    @Test
    void testUpdateCategoryNameIfCategoryNotFound() {
        CategoryUpdateDto categoryUpdateDto = CategoryUpdateDto.builder()
                .categoryName("testCategorySecond")
                .build();

        Exception exception = assertThrows(NotFoundException.class, () -> categoryService.updateCategory(100000, categoryUpdateDto));
        String messageException = "Category with id = " + 100000 + " not found";
        assertEquals(messageException, exception.getMessage());
    }

    @Test
    void testUpdateCategoryNameIfIdNull() {
        CategoryUpdateDto categoryUpdateDto = CategoryUpdateDto.builder()
                .categoryName("testCategorySecond")
                .build();

        Exception exception = assertThrows(InvalidDataAccessApiUsageException.class, () -> categoryService.updateCategory(null, categoryUpdateDto));
        String messageException = "The given id must not be null";
        assertEquals(messageException, exception.getMessage());
    }


    @Test
    void testUpdateCategoryNameIfNameTooLong() {
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

        CategoryUpdateDto categoryUpdateDto = CategoryUpdateDto.builder()
                .categoryName("testCategorySecondTestCategorySecondTestCategorySecondTestCategorySecondTestCategorySecondTestCategorySecond")
                .build();

        Exception exception = assertThrows(IllegalArgumentException.class, () -> categoryService.updateCategory(categoryOne.getCategoryId(), categoryUpdateDto));
        String messageException = "Category name must be between 3 and 20 characters";
        assertEquals(messageException, exception.getMessage());
    }

    @Test
    void testUpdateCategoryNameIfNameTooShort() {
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

        CategoryUpdateDto categoryUpdateDto = CategoryUpdateDto.builder()
                .categoryName("te")
                .build();

        Exception exception = assertThrows(IllegalArgumentException.class, () -> categoryService.updateCategory(categoryOne.getCategoryId(), categoryUpdateDto));
        String messageException = "Category name must be between 3 and 20 characters";
        assertEquals(messageException, exception.getMessage());
    }

}