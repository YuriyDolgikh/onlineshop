package org.onlineshop.service.interfaces;

import org.onlineshop.dto.category.CategoryRequestDto;
import org.onlineshop.dto.category.CategoryResponseDto;
import org.onlineshop.dto.category.CategoryUpdateDto;
import org.onlineshop.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CategoryServiceInterface {

    CategoryResponseDto addCategory(CategoryRequestDto categoryRequestDto);

    CategoryResponseDto updateCategory(Integer categoryId, CategoryUpdateDto categoryUpdateDto);

    CategoryResponseDto deleteCategory(Integer categoryId);

    Page<CategoryResponseDto> getAllCategories(Pageable pageable);

    Category getCategoryById(Integer categoryId);

    Category getCategoryByName(String categoryName);

}