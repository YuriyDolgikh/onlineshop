package org.onlineshop.service;

import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintDeclarationException;
import lombok.RequiredArgsConstructor;
import org.onlineshop.dto.category.CategoryRequestDto;
import org.onlineshop.dto.category.CategoryResponseDto;
import org.onlineshop.dto.category.CategoryUpdateDto;
import org.onlineshop.entity.Category;
import org.onlineshop.entity.Favourite;
import org.onlineshop.exception.BadRequestException;
import org.onlineshop.repository.CategoryRepository;
import org.onlineshop.service.converter.CategoryConverter;
import org.onlineshop.service.interfaces.CategoryServiceInterface;
import org.onlineshop.service.util.CategoryServiceHelper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

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
        String categoryName = categoryRequestDto.getCategoryName().trim();
        if (categoryName.length() < 3 || categoryName.length() > 20) {
            throw new IllegalArgumentException("Category name must be between 3 and 20 characters");
        }

        final String finalImage = helper.resolveImageUrl(categoryRequestDto.getImage());
        Category category = Category.builder()
                .categoryId(null)
                .categoryName(categoryRequestDto.getCategoryName().trim())
                .image(finalImage)
                .build();

        try {
            Category savedCategory = categoryRepository.save(category);
            return categoryConverter.toDto(savedCategory);
        } catch (DataIntegrityViolationException exception) {
            throw new BadRequestException("Category with this name already exists");
        }
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
            try{
                categoryForUpdate.setCategoryName(categoryUpdateDto.getCategoryName().trim());
            }catch (ConstraintDeclarationException exception) {
                throw new BadRequestException("Category with this name already exists");
            }

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
     * Retrieves a page of all categories from the database.
     *
     * @param pageable the pagination information
     * @return a page of CategoryResponseDto objects, each containing details of a category
     */
    @Override
    public Page<CategoryResponseDto> getAllCategories(Pageable pageable) {
        return categoryRepository.findAll(pageable)
                .map(categoryConverter::toDto);
    }

    /**
     * Retrieves a category from the database based on the provided category ID.
     *
     * @param categoryId the ID of the category to be retrieved
     * @return the Category object corresponding to the specified ID
     * @throws BadRequestException if a category with the specified ID is not found in the database
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
     * @return the Category object corresponding to the specified name
     * @throws BadRequestException if a category with the specified name is not found in the database
     */
    public Category getCategoryByName(String categoryName) {
        return categoryRepository.findByCategoryName(categoryName)
                .orElseThrow(() -> new BadRequestException("Category with name: " + categoryName + " not found"));
    }
}