package org.onlineshop.controller;

import lombok.RequiredArgsConstructor;
import org.onlineshop.dto.category.CategoryRequestDto;
import org.onlineshop.dto.category.CategoryResponseDto;
import org.onlineshop.dto.category.CategoryUpdateDto;
import org.onlineshop.service.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/categories")
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<CategoryResponseDto> addCategory(@RequestBody CategoryRequestDto categoryRequestDto){
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(categoryService.addCategory(categoryRequestDto));
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<CategoryResponseDto> updateCategory(@PathVariable Integer categoryId, @RequestBody CategoryUpdateDto categoryUpdateDto){
        return ResponseEntity.ok(categoryService.updateCategory(categoryId, categoryUpdateDto));
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<CategoryResponseDto> deleteCategory(@PathVariable Integer categoryId){
        return ResponseEntity.ok(categoryService.deleteCategory(categoryId));
    }

    @GetMapping
    public ResponseEntity<Iterable<CategoryResponseDto>> getAllCategories(){
        return ResponseEntity.ok(categoryService.getAllCategories());
    }
}
