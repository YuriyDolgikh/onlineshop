package org.onlineshop.service;

import org.onlineshop.dto.category.CategoryRequestDto;
import org.onlineshop.dto.category.CategoryResponseDto;

import java.util.List;

public interface CategoryServiceInterface {

    CategoryResponseDto addCategory(CategoryRequestDto categoryRequestDto);

    CategoryResponseDto updateCategory(Integer categoryId, CategoryRequestDto categoryRequestDto);

    CategoryResponseDto deleteCategory(Integer categoryId);

    List<CategoryResponseDto> getAllCategories();

}
