package org.onlineshop.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.onlineshop.dto.category.CategoryRequestDto;
import org.onlineshop.dto.category.CategoryResponseDto;
import org.onlineshop.entity.Category;
import org.onlineshop.exception.BadRequestException;
import org.onlineshop.repository.CategoryRepository;
import org.onlineshop.service.converter.CategoryConverter;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryService implements CategoryServiceInterface {

    private final CategoryRepository categoryRepository;
    private final CategoryConverter categoryConverter;

    @Transactional
    @Override
    public CategoryResponseDto addCategory(CategoryRequestDto categoryRequestDto) {
        if (categoryRequestDto == null){
            throw new IllegalArgumentException("Category request must be provided");
        }
        if (categoryRequestDto.getCategoryName() == null || categoryRequestDto.getCategoryName().isBlank()){
            throw new IllegalArgumentException("Category name must be provided");
        }
        if (categoryRepository.existsByCategoryName(categoryRequestDto.getCategoryName())){
            throw new BadRequestException("Category with name: " + categoryRequestDto.getCategoryName() + " already exist");
        }
        Category category = Category.builder()
                .categoryId(null)
                .categoryName(categoryRequestDto.getCategoryName())
                .build();
        Category savedCategory = categoryRepository.save(category);
        return categoryConverter.toDto(savedCategory);
    }

    @Transactional
    @Override
    public CategoryResponseDto updateCategory(Integer categoryId, CategoryRequestDto categoryRequestDto) {
        if (categoryId == null){
            throw new IllegalArgumentException("Category id must be provided");
        }
        if (categoryRequestDto == null){
            throw new IllegalArgumentException("Category request must be provided");
        }
        if (categoryRequestDto.getCategoryName() == null || categoryRequestDto.getCategoryName().isBlank()){
            throw new IllegalArgumentException("Category name must be provided");
        }
        Optional<Category> categoryOptional = categoryRepository.findById(categoryId);
        if (categoryOptional.isEmpty()){
            throw new BadRequestException("Category with id: " + categoryId + " not found");
        }
        Category categoryToUpdate = categoryOptional.get();
        categoryToUpdate.setCategoryName(categoryRequestDto.getCategoryName());
        Optional<Category> categoryForCheck = categoryRepository.findByCategoryName(categoryRequestDto.getCategoryName());
        if (categoryForCheck.isPresent() && !categoryForCheck.get().getCategoryId().equals(categoryId)){
            throw new BadRequestException("Category with name: " + categoryRequestDto.getCategoryName() + " already exist");
        }
        Category savedCategory = categoryRepository.save(categoryToUpdate);
        return categoryConverter.toDto(savedCategory);
    }

    @Transactional
    @Override
    public CategoryResponseDto deleteCategory(Integer categoryId) {
        if (categoryId == null){
            throw new IllegalArgumentException("Category id must be provided");
        }
        Category categoryToDelete = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new BadRequestException("Category with id: " + categoryId + " not found"));
        categoryToDelete.getProducts()
                .stream()
                .forEach(product -> product.setCategory(null));      //TODO: null must be changed to DEFAULT category
        categoryRepository.delete(categoryToDelete);
        return categoryConverter.toDto(categoryToDelete);
    }

    @Override
    public List<CategoryResponseDto> getAllCategories() {
        return categoryConverter.fromCategories(categoryRepository.findAll());
    }

}
