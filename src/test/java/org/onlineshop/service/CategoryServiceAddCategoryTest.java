package org.onlineshop.service;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.onlineshop.dto.category.CategoryRequestDto;
import org.onlineshop.dto.category.CategoryResponseDto;
import org.onlineshop.entity.Category;
import org.onlineshop.exception.BadRequestException;
import org.onlineshop.exception.UrlValidationError;
import org.onlineshop.exception.UrlValidationException;
import org.onlineshop.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.ArrayList;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.yml")
class CategoryServiceAddCategoryTest {

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

    @BeforeEach
    void setUp() {

        Category category = Category.builder()
                .categoryName("categoryForOtherTest")
                .image("https://drive.google.com/file/d/1y03Ct0ABP1X8O6NFvK6FdqiMacYpLeTs/view?usp=drive_link")
                .products(new ArrayList<>())
                .build();

        categoryRepository.save(category);

    }

    @Test
    void testAddCategoryIfOk() {
        CategoryRequestDto categoryRequestDto = CategoryRequestDto.builder()
                .categoryName("TestCategory")
                .image("https://drive.google.com/file/two")
                .build();

        CategoryResponseDto category = categoryService.addCategory(categoryRequestDto);
        assertNotNull(category);
        assertEquals(categoryRequestDto.getCategoryName(), category.getCategoryName());
        assertEquals(categoryRequestDto.getImage(), category.getImage());
        assertEquals(2, categoryRepository.findAll().size());
    }

    @Test
    void testAddCategoryIfCategoryNameIsBlank() {
        CategoryRequestDto categoryRequestDto = CategoryRequestDto.builder()
                .categoryName(" ")
                .image("https://drive.google.com/file/two")
                .build();

        Exception exception = assertThrows(IllegalArgumentException.class, () -> categoryService.addCategory(categoryRequestDto));
        String messageException = "Category name must be provided";
        assertEquals(messageException, exception.getMessage());
    }

    @Test
    void testAddCategoryIfCategoryNameIsNull() {
        CategoryRequestDto categoryRequestDto = CategoryRequestDto.builder()
                .categoryName(null)
                .image("https://drive.google.com/file/two")
                .build();

        Exception exception = assertThrows(IllegalArgumentException.class, () -> categoryService.addCategory(categoryRequestDto));
        String messageException = "Category name must be provided";
        assertEquals(messageException, exception.getMessage());
    }

    @Test
    void testAddCategoryIfRequestDtoIsNull() {
        CategoryRequestDto categoryRequestDto = null;

        Exception exception = assertThrows(IllegalArgumentException.class, () -> categoryService.addCategory(categoryRequestDto));
        String messageException = "Category request must be provided";
        assertEquals(messageException, exception.getMessage());
    }

    @Test
    void testAddCategoryIfCategoryNameIsAlreadyExist() {
        CategoryRequestDto categoryRequestDto = CategoryRequestDto.builder()
                .categoryName("categoryForOtherTest")
                .image("https://drive.google.com/file/two")
                .build();

        assertThrows(DataIntegrityViolationException.class, () -> categoryService.addCategory(categoryRequestDto));
    }

    @Test
    void testAddCategoryIfNameIsTooShort() {
        CategoryRequestDto categoryRequestDto = CategoryRequestDto.builder()
                .categoryName("te")
                .image("https://drive.google.com/file/two")
                .build();

        Set<ConstraintViolation<CategoryRequestDto>> violations = validatorFactory.getValidator().validate(categoryRequestDto);
        assertFalse(violations.isEmpty(), "Validation should fail for too short category name");
        assertTrue(
                violations.stream().anyMatch(v -> v.getMessage().equals("Category name must be between 3 and 20 characters")),
                "Error message should be 'Category name must be between 3 and 20 characters'"
        );
    }

    @Test
    void testAddCategoryIfNameIsTooLong() {
        CategoryRequestDto categoryRequestDto = CategoryRequestDto.builder()
                .categoryName("testCategoryForOtherTestTestCategoryForOtherTestTestCategoryForOtherTestTestCategoryForOtherTest")
                .image("https://drive.google.com/file/two")
                .build();

        Set<ConstraintViolation<CategoryRequestDto>> violations = validatorFactory.getValidator().validate(categoryRequestDto);
        assertFalse(violations.isEmpty(), "Validation should fail for too short category name");
        assertTrue(
                violations.stream().anyMatch(v -> v.getMessage().equals("Category name must be between 3 and 20 characters")),
                "Error message should be 'Category name must be between 3 and 20 characters'"
        );
    }

    @Test
    void testAddCategoryIfImageUrlInvalid() {
        String uniqueName = "testCategory";

        CategoryRequestDto dto = CategoryRequestDto.builder()
                .categoryName(uniqueName)
                .image("INVALID")
                .build();

        UrlValidationException ex = assertThrows(
                UrlValidationException.class,
                () -> categoryService.addCategory(dto),
                "Service should throw UrlValidationException for invalid image URL"
        );

        UrlValidationError err = ex.getError();
        assertTrue(
                err == UrlValidationError.INVALID_DOMAIN
                        || err == UrlValidationError.INVALID_EXTENSION
                        || err == UrlValidationError.UNREACHABLE,
                () -> "Unexpected error type for invalid image URL: " + err
        );
    }

}