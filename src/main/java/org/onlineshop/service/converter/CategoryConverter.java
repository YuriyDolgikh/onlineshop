package org.onlineshop.service.converter;

import lombok.Generated;
import lombok.RequiredArgsConstructor;
import org.onlineshop.dto.category.CategoryRequestDto;
import org.onlineshop.dto.category.CategoryResponseDto;
import org.onlineshop.entity.Category;
import org.springframework.stereotype.Service;

import java.util.List;

@Generated
@Service
@RequiredArgsConstructor
public class CategoryConverter {

    public Category fromDto(CategoryRequestDto categoryRequestDto){
        return Category.builder()
                .categoryName(categoryRequestDto.getCategoryName())
                .image(categoryRequestDto.getImage())
                .build();
    }

    public CategoryResponseDto toDto(Category category){
        return CategoryResponseDto.builder()
                .categoryId(category.getCategoryId())
                .categoryName(category.getCategoryName())
                .image(category.getImage())
                .build();
    }

    public List<CategoryResponseDto> toDtos(List<Category> categories){
        return categories.stream()
                .map(this::toDto)
                .toList();
    }
}
