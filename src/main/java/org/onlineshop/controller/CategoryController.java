package org.onlineshop.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.onlineshop.dto.category.CategoryRequestDto;
import org.onlineshop.dto.category.CategoryResponseDto;
import org.onlineshop.dto.category.CategoryUpdateDto;
import org.onlineshop.service.CategoryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/categories")
@Tag(name = "Category Management", description = "APIs for category management operations including CRUD operations")
public class CategoryController {

    private final CategoryService categoryService;

    /**
     * Adds a new category to the system based on the details provided in the request.
     *
     * @param categoryRequestDto the category details encapsulated in a {@code CategoryRequestDto}
     * @return a {@code ResponseEntity} containing a {@code CategoryResponseDto} with details of the newly created category
     */
    @Operation(
            summary = "Add a new category",
            description = "Creates a new category in the system with the provided details."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Category successfully created",
                    content = @Content(schema = @Schema(implementation = CategoryResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request - invalid input data or category already exists"
            )
    })
    @PostMapping
    public ResponseEntity<CategoryResponseDto> addCategory(
            @Parameter(description = "Category creation data", required = true)
            @Valid @RequestBody CategoryRequestDto categoryRequestDto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(categoryService.addCategory(categoryRequestDto));
    }

    /**
     * Updates an existing category with the specified ID based on the details provided in the update request.
     *
     * @param categoryId        the ID of the category to be updated
     * @param categoryUpdateDto the category update details encapsulated in a {@code CategoryUpdateDto}
     * @return a {@code ResponseEntity} containing a {@code CategoryResponseDto} with the updated category details
     */
    @Operation(
            summary = "Update category",
            description = "Updates an existing category with the specified ID based on the provided update data."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Category successfully updated",
                    content = @Content(schema = @Schema(implementation = CategoryResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request - invalid input data"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not found - category not found"
            )
    })
    @PutMapping("/{categoryId}")
    public ResponseEntity<CategoryResponseDto> updateCategory(
            @Parameter(description = "ID of the category to update", required = true)
            @PathVariable Integer categoryId,
            @Parameter(description = "Updated category data", required = true)
            @Valid @RequestBody CategoryUpdateDto categoryUpdateDto) {
        return ResponseEntity.ok(categoryService.updateCategory(categoryId, categoryUpdateDto));
    }

    /**
     * Deletes an existing category with the specified ID.
     *
     * @param categoryId the ID of the category to be deleted
     * @return a {@code ResponseEntity} containing a {@code CategoryResponseDto} with the deleted category details
     */
    @Operation(
            summary = "Delete category",
            description = "Deletes a category from the system. Products associated with this category will have their category set to null."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Category successfully deleted",
                    content = @Content(schema = @Schema(implementation = CategoryResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request - invalid category ID"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not found - category not found"
            )
    })
    @DeleteMapping("/{categoryId}")
    public ResponseEntity<CategoryResponseDto> deleteCategory(
            @Parameter(description = "ID of the category to delete", required = true)
            @PathVariable Integer categoryId) {
        return ResponseEntity.ok(categoryService.deleteCategory(categoryId));
    }

    /**
     * Retrieves all categories from the system with pagination.
     *
     * @param page the page number (0-based)
     * @param size the page size
     * @return a {@code ResponseEntity} containing a {@code Page<CategoryResponseDto>}
     * containing all categories available in the system
     */
    @Operation(
            summary = "Get all categories",
            description = "Retrieves a paginated list of all categories available in the system."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Categories retrieved successfully",
                    content = @Content(schema = @Schema(implementation = Page.class))
            )
    })
    @GetMapping
    public ResponseEntity<Page<CategoryResponseDto>> getAllCategories(
            @Parameter(description = "Page number (0-based)", example = "0")
            @Min(0) @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "20")
            @Min(1) @Max(100) @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(categoryService.getAllCategories(pageable));
    }
}