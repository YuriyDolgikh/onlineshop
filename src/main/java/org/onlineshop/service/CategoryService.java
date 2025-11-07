package org.onlineshop.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.onlineshop.dto.category.CategoryRequestDto;
import org.onlineshop.dto.category.CategoryResponseDto;
import org.onlineshop.dto.category.CategoryUpdateDto;
import org.onlineshop.entity.Category;
import org.onlineshop.entity.Product;
import org.onlineshop.exception.BadRequestException;
import org.onlineshop.repository.CategoryRepository;
import org.onlineshop.service.converter.CategoryConverter;
import org.onlineshop.service.interfaces.CategoryServiceInterface;
import org.onlineshop.service.util.CategoryServiceHelper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryService implements CategoryServiceInterface {

    private final CategoryRepository categoryRepository;
    private final CategoryConverter categoryConverter;
    private final CategoryServiceHelper helper;

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
            categoryForUpdate.setCategoryName(categoryUpdateDto.getCategoryName());
        }

        if (categoryUpdateDto.getImage() != null && !categoryUpdateDto.getImage().isBlank()) {
            categoryForUpdate.setImage(categoryUpdateDto.getImage());
        }
        Category savedCategory = categoryRepository.save(categoryForUpdate);
        return categoryConverter.toDto(savedCategory);
    }

    @Transactional
    @Override
    public CategoryResponseDto deleteCategory(Integer categoryId) {
        if (categoryId == null) {
            throw new IllegalArgumentException("Category id must be provided");
        }
        Category categoryToDelete = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new BadRequestException("Category with id: " + categoryId + " not found"));
        categoryToDelete.getProducts()
                .forEach(product -> product.setCategory(null));      //TODO: null must be changed to OTHER category
        categoryRepository.delete(categoryToDelete);
        return categoryConverter.toDto(categoryToDelete);
    }

    @Override
    public List<CategoryResponseDto> getAllCategories() {
        return categoryConverter.toDtos(categoryRepository.findAll());
    }

    @Override
    public Category getCategoryById(Integer categoryId) {
        return categoryRepository.findByCategoryId(categoryId)
                .orElseThrow(() -> new BadRequestException("Category with id: " + categoryId + " not found"));
    }

    public Category getCategoryByName(String categoryName) {
        return categoryRepository.findByCategoryName(categoryName)
                .orElseThrow(() -> new BadRequestException("Category with name: " + categoryName + " not found"));
    }

}