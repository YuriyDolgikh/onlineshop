package org.onlineshop.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.onlineshop.dto.product.ProductRequestDto;
import org.onlineshop.dto.product.ProductResponseDto;
import org.onlineshop.dto.product.ProductResponseForUserDto;
import org.onlineshop.dto.product.ProductUpdateDto;
import org.onlineshop.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/products")
@Tag(name = "Product Management",
        description = "APIs for product management operations including CRUD, discounts, and product filtering")
public class ProductController {

    private final ProductService productService;

    /**
     * Adds a new product to the system based on the details provided in the request.
     * Only users with roles 'MANAGER' or 'ADMIN' are authorized to perform this operation.
     *
     * @param requestDto the product details encapsulated in a {@code ProductRequestDto}
     *                   containing fields such as product name, description, category, price, etc.
     * @return a {@code ResponseEntity} containing a {@code ProductResponseDto} with details of the newly created product
     * and a status of {@code HttpStatus.CREATED}.
     */
    @Operation(
            summary = "Add a new product",
            description = "Creates a new product in the system. Requires MANAGER or ADMIN role."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Product successfully created",
                    content = @Content(schema = @Schema(implementation = ProductResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request - invalid input data or product already exists in category"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - insufficient permissions"
            )
    })
    @PostMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<ProductResponseDto> addNewProduct(
            @Parameter(description = "Product creation data", required = true)
            @Valid @RequestBody ProductRequestDto requestDto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(productService.addProduct(requestDto));

    }

    /**
     * Updates an existing product with the specified ID based on the details provided in the update request.
     * Only users with roles 'MANAGER' or 'ADMIN' are authorized to perform this operation.
     *
     * @param productId the ID of the product to be updated
     * @param updateDto the product update details encapsulated in a {@code ProductUpdateDto}
     *                  containing fields such as product name, description, category, price, etc.
     * @return a {@code ResponseEntity} containing a {@code ProductResponseDto} with the updated product details
     * and a status of {@code HttpStatus.OK}.
     */
    @Operation(
            summary = "Update product",
            description = "Updates an existing product with the specified ID. Requires MANAGER or ADMIN role."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Product successfully updated",
                    content = @Content(schema = @Schema(implementation = ProductResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request - invalid input data"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not found - product not found"
            )
    })
    @PutMapping("{productId}")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<ProductResponseDto> updateProduct(
            @Parameter(description = "ID of the product to update", required = true)
            @Valid @PathVariable Integer productId,
            @Parameter(description = "Updated product data", required = true)
            @Valid @RequestBody ProductUpdateDto updateDto) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(productService.updateProduct(productId, updateDto));
    }

    /**
     * Deletes an existing product with the specified ID.
     *
     * @param productId the ID of the product to be deleted
     * @return a {@code ResponseEntity} containing a {@code ProductResponseDto} with the deleted product details
     */
    @Operation(
            summary = "Delete product",
            description = "Deletes a product from the system. Requires MANAGER or ADMIN role."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Product successfully deleted",
                    content = @Content(schema = @Schema(implementation = ProductResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not found - product not found"
            )
    })
    @DeleteMapping("{productId}")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<ProductResponseDto> deleteProduct(
            @Parameter(description = "ID of the product to delete", required = true)
            @Valid @PathVariable Integer productId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(productService.deleteProduct(productId));
    }

    /**
     * Updates the discount for an existing product with the specified product ID.
     * Only users with roles 'MANAGER' or 'ADMIN' are authorized to perform this operation.
     *
     * @param productId   the ID of the product for which the discount is being updated
     * @param newDiscount the new discount value to be applied to the product
     * @return a {@code ResponseEntity} containing a {@code ProductResponseDto}
     * with the updated product details and a status of {@code HttpStatus.OK}
     */
    @Operation(
            summary = "Update product discount",
            description = "Updates the discount price for a specific product. Requires MANAGER or ADMIN role."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Discount successfully updated",
                    content = @Content(schema = @Schema(implementation = ProductResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request - invalid discount value"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not found - product not found"
            )
    })
    @GetMapping("/updateProductDiscount/{productId}/{newDiscount}")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<ProductResponseDto> updateProductDiscount(
            @Parameter(description = "ID of the product to update discount", required = true)
            @Valid @PathVariable Integer productId,
            @Parameter(description = "New discount price value", required = true, example = "15.99")
            @Valid @PathVariable BigDecimal newDiscount) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(productService.setDiscountPrice(productId, newDiscount));
    }

    /**
     * Retrieves a list of all products available in the system.
     * Only users with roles 'MANAGER' or 'ADMIN' are authorized to perform this operation.
     *
     * @return a {@code ResponseEntity} containing a {@code List<ProductResponseDto>}
     * that includes details of all products and a status of {@code HttpStatus.OK}.
     */
    @Operation(
            summary = "Get all products (Admin/Manager)",
            description = "Retrieves a list of all products with full details. Requires MANAGER or ADMIN role."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Products retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ProductResponseDto.class))
            )
    })
    @GetMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<List<ProductResponseDto>> getAllProductsForAdmin() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(productService.getAllProducts());
    }

    /**
     * Retrieves a list of products filtered and optionally sorted based on the specified criteria.
     *
     * @param paramName     the name of the parameter to filter the products by (e.g., category, brand, etc.)
     * @param paramValue    the value of the parameter to filter against
     * @param sortDirection the direction of sorting, either "asc" for ascending or "desc" for descending
     * @return a {@code ResponseEntity} containing a {@code List<ProductResponseDto>} with the filtered and sorted product details, and a status of {@code HttpStatus.OK}
     */
    @Operation(
            summary = "Get products by criteria",
            description = "Retrieves products filtered and sorted by various criteria. Available criteria: price, discount, category, name, createDate"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Products retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ProductResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request - invalid criteria parameters"
            )
    })
    @GetMapping("/getProductsByCriteria")
    public ResponseEntity<List<ProductResponseDto>> getProductsByCriteria(
            @Parameter(
                    description = "Criteria name: price, discount, category, name, createDate",
                    required = true,
                    examples = {
                            @io.swagger.v3.oas.annotations.media.ExampleObject(
                                    name = "Price range",
                                    value = "price"
                            ),
                            @io.swagger.v3.oas.annotations.media.ExampleObject(
                                    name = "Discount",
                                    value = "discount"
                            ),
                            @io.swagger.v3.oas.annotations.media.ExampleObject(
                                    name = "Category",
                                    value = "category"
                            )
                    }
            )
            @Valid @RequestParam String paramName,
            @Parameter(
                    description = "Criteria value (for price use format: minPrice-maxPrice, e.g., 100-300)",
                    required = true,
                    examples = {
                            @io.swagger.v3.oas.annotations.media.ExampleObject(
                                    name = "Price range",
                                    value = "100-300"
                            ),
                            @io.swagger.v3.oas.annotations.media.ExampleObject(
                                    name = "Category name",
                                    value = "electronics"
                            ),
                            @io.swagger.v3.oas.annotations.media.ExampleObject(
                                    name = "Product name part",
                                    value = "phone"
                            )
                    }
            )
            @Valid @RequestParam String paramValue,
            @Parameter(
                    description = "Sort direction: asc (ascending) or desc (descending)",
                    required = true,
                    examples = {
                            @io.swagger.v3.oas.annotations.media.ExampleObject(
                                    name = "Ascending",
                                    value = "asc"
                            ),
                            @io.swagger.v3.oas.annotations.media.ExampleObject(
                                    name = "Descending",
                                    value = "desc"
                            )
                    }
            )
            @Valid @RequestParam String sortDirection) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(productService.getProductsByCriteria(paramName, paramValue, sortDirection));
    }

    /**
     * Retrieves a list of all products available in the system for the current logged-in user.
     *
     * @return a {@code ResponseEntity} containing a {@code List<ProductResponseForUserDto>}
     */
    @Operation(
            summary = "Get all products for users",
            description = "Retrieves a list of all products with user-friendly details (without internal IDs and timestamps)."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "302",
                    description = "Products found and retrieved",
                    content = @Content(schema = @Schema(implementation = ProductResponseForUserDto.class))
            )
    })
    @GetMapping("/getAllProductForUser")
    public ResponseEntity<List<ProductResponseForUserDto>> getAllProductForUser() {
        return ResponseEntity
                .status(HttpStatus.FOUND)
                .body(productService.getAllProductsForUser());
    }

    /**
     * Retrieves the top five products with the highest discounts available for the current day.
     * The products are sorted in descending order based on their discount percentages.
     *
     * @return a {@code ResponseEntity} containing a {@code List<ProductResponseForUserDto>}
     * that includes the details of the top five discounted products and
     * a status of {@code HttpStatus.FOUND}.
     */
    @Operation(
            summary = "Get top five discounted products",
            description = "Retrieves the top five products with the highest discounts available for the current day."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "302",
                    description = "Top discounted products found",
                    content = @Content(schema = @Schema(implementation = ProductResponseForUserDto.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "No discounted products found"
            )
    })
    @GetMapping("/getTopFiveProducts")
    public ResponseEntity<List<ProductResponseForUserDto>> getTopFiveDiscountedProductsOfTheDay() {
        return ResponseEntity
                .status(HttpStatus.FOUND)
                .body(productService.getTopFiveDiscountedProductsOfTheDay());
    }
}