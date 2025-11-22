package org.onlineshop.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.onlineshop.dto.category.CategoryRequestDto;
import org.onlineshop.dto.category.CategoryResponseDto;
import org.onlineshop.dto.category.CategoryUpdateDto;
import org.onlineshop.entity.Category;
import org.onlineshop.exception.BadRequestException;
import org.onlineshop.repository.CategoryRepository;
import org.onlineshop.service.converter.CategoryConverter;
import org.onlineshop.service.interfaces.CategoryServiceInterface;
import org.onlineshop.service.util.CategoryServiceHelper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service class for managing Category entities. This class provides operations
 * for adding, updating, deleting, and retrieving category information while
 * ensuring business rules and validations are enforced.
 */
@Service
@RequiredArgsConstructor
public class CategoryService implements CategoryServiceInterface {

    private final CategoryRepository categoryRepository;
    private final CategoryConverter categoryConverter;
    private final CategoryServiceHelper helper;

    /**
     * Adds a new category based on the provided category request data.
     *
     * @param categoryRequestDto the data transfer object containing the details of the category to be added
     * @return a CategoryResponseDto containing the details of the newly added category
     * @throws IllegalArgumentException if the provided categoryRequestDto is null,
     *                                  or if the category name is null or blank
     * @throws BadRequestException      if a category with the same name already exists
     */
    @Transactional
    @Override
    public CategoryResponseDto addCategory(CategoryRequestDto categoryRequestDto) {
        if (categoryRequestDto == null) {
            throw new IllegalArgumentException("Category request must be provided");
        }
        if (categoryRequestDto.getCategoryName() == null || categoryRequestDto.getCategoryName().isBlank()) {
            throw new IllegalArgumentException("Category name must be provided");
        }
        if (categoryRepository.existsByCategoryName(categoryRequestDto.getCategoryName())) {
            throw new BadRequestException("Category with name: " + categoryRequestDto.getCategoryName() + " already exist");
        }

        final String finalImage = helper.resolveImageUrl(categoryRequestDto.getImage());
        Category category = Category.builder()
                .categoryId(null)
                .categoryName(categoryRequestDto.getCategoryName().trim())
                .image(finalImage)
                .build();

        Category savedCategory = categoryRepository.save(category);
        return categoryConverter.toDto(savedCategory);
    }

    /**
     * Updates an existing category based on the provided category ID and update data.
     * The method validates and updates fields like category name and image if provided.
     *
     * @param categoryId        the ID of the category to be updated
     * @param categoryUpdateDto the data transfer object containing the updated details of the category
     * @return a CategoryResponseDto containing the details of the updated category
     * @throws IllegalArgumentException if the provided category ID is not found in the database,
     *                                  or if the updated category name is invalid
     * @throws BadRequestException      if a category with the updated name already exists with a different ID
     */
    @Transactional
    @Override
    public CategoryResponseDto updateCategory(Integer categoryId, CategoryUpdateDto categoryUpdateDto) {
        Category categoryForUpdate = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Category with id = " + categoryId + " not found"));

        if (categoryUpdateDto.getCategoryName() != null && !categoryUpdateDto.getCategoryName().isBlank()) {
            if (categoryUpdateDto.getCategoryName().length() < 3 || categoryUpdateDto.getCategoryName().length() > 20) {
                throw new IllegalArgumentException("Category name must be between 3 and 20 characters");
            }
            Optional<Category> categoryForCheck = categoryRepository.findByCategoryName(categoryUpdateDto.getCategoryName());
            if (categoryForCheck.isPresent() && !categoryForCheck.get().getCategoryId().equals(categoryId)) {
                throw new BadRequestException("Category with name: " + categoryUpdateDto.getCategoryName() + " already exist");
            }
            categoryForUpdate.setCategoryName(categoryUpdateDto.getCategoryName().trim());
        }

        if (categoryUpdateDto.getImage() != null && !categoryUpdateDto.getImage().isBlank()) {
            String newImage = helper.resolveImageUrl(categoryUpdateDto.getImage());
            categoryForUpdate.setImage(newImage);
        }
        Category savedCategory = categoryRepository.save(categoryForUpdate);
        return categoryConverter.toDto(savedCategory);
    }

    /**
     * Deletes an existing category based on the provided category ID.
     *
     * @param categoryId the ID of the category to be deleted from the database
     * @return a CategoryResponseDto containing the details of the deleted category
     * @throws IllegalArgumentException if the provided category ID is null
     * @throws BadRequestException      if the category cannot be deleted because it is associated with one or more products
     */
    @Transactional
    @Override
    public CategoryResponseDto deleteCategory(Integer categoryId) {
        if (categoryId == null) {
            throw new IllegalArgumentException("Category id must be provided");
        }
        Category categoryToDelete = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new BadRequestException("Category with id: " + categoryId + " not found"));
        categoryToDelete.getProducts()
                .forEach(product -> product.setCategory(categoryRepository.findByCategoryName("Other")
                        .orElseThrow(() -> new BadRequestException("Other category not found"))));
        categoryRepository.delete(categoryToDelete);
        return categoryConverter.toDto(categoryToDelete);
    }

    /**
     * Retrieves all categories from the database.
     *
     * @return a list of CategoryResponseDto objects containing the details of all categories in the database
     */
    @Override
    public List<CategoryResponseDto> getAllCategories() {
        return categoryConverter.toDtos(categoryRepository.findAll());
    }

    /**
     * Retrieves a specific category based on the provided category ID.
     *
     * @param categoryId the ID of the category to be retrieved from the database
     * @return a CategoryResponseDto containing the details of the retrieved category
     * @throws BadRequestException if the provided category ID is not found in the database
     */
    @Override
    public Category getCategoryById(Integer categoryId) {
        return categoryRepository.findByCategoryId(categoryId)
                .orElseThrow(() -> new BadRequestException("Category with id: " + categoryId + " not found"));
    }

    /**
     * Retrieves a specific category based on the provided category name.
     *
     * @param categoryName the name of the category to be retrieved from the database
     * @return a CategoryResponseDto containing the details of the retrieved category
     * @throws BadRequestException if the provided category name is not found in the database
     */
    public Category getCategoryByName(String categoryName) {
        return categoryRepository.findByCategoryName(categoryName)
                .orElseThrow(() -> new BadRequestException("Category with name: " + categoryName + " not found"));
    }
}