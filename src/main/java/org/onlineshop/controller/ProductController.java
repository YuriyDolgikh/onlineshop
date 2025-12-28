package org.onlineshop.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
        description = "APIs for product management operations including CRUD, discounts, and product filtering with pagination")
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
            @PathVariable Integer productId,
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
            @PathVariable Integer productId) {
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
            @PathVariable Integer productId,
            @Parameter(description = "New discount price value", required = true, example = "15.99")
            @PathVariable BigDecimal newDiscount) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(productService.setDiscountPrice(productId, newDiscount));
    }

    /**
     * Retrieves a paginated list of all products with full details for Admin or Manager roles.
     * This method is secured and requires the user to have either MANAGER or ADMIN role.
     *
     * @param page the page number for pagination (0-based index, default is 0).
     * @param size the size of each page for pagination (default is 20).
     * @param sort the field to sort the results by (default is "name").
     * @param direction in the direction of sorting, either "asc" for ascending or "desc" for descending (default is "asc").
     * @return a ResponseEntity containing a Page of ProductResponseDto objects with the product details.
     */
    @Operation(summary = "Get all products (Admin/Manager)",
            description = "Retrieves a list of all products with full details. Requires MANAGER or ADMIN role.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Products retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ProductResponseDto.class)))
    })
    @GetMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<Page<ProductResponseDto>> getAllProductsForAdmin(
            @Parameter(description = "Page number (0-based)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "20") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "name") String sort,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "asc") String direction) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(direction), sort));
        return ResponseEntity.ok(productService.getAllProducts(pageable));
    }

    /**
     * Retrieves products filtered and sorted by specified criteria.
     * Available criteria include price, discount, category, name, and createDate.
     *
     * @param paramName the name of the filtering criteria (e.g., price, discount, category, name, createDate)
     * @param paramValue the value of the filtering criteria. For price, use the format: minPrice-maxPrice (e.g., 100-300)
     * @param sortDirection in the sort direction, either "asc" for ascending or "desc" for descending
     * @param page the page number (0-based index)
     * @param size the number of items per page
     * @return a ResponseEntity containing a pageable list of ProductResponseDto objects that match the specified criteria
     */
    @Operation(summary = "Get products by criteria",
            description = "Retrieves products filtered and sorted by various criteria. Available criteria: price, discount, category, name, createDate")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Products retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ProductResponseDto.class))),
            @ApiResponse(responseCode = "400",
                    description = "Bad request - invalid criteria parameters")
    })
    @GetMapping("/getProductsByCriteria")
    public ResponseEntity<Page<ProductResponseDto>> getProductsByCriteria(
            @Parameter(description = "Criteria name: price, discount, category, name, createDate", required = true, examples = {
                    @ExampleObject(name = "Price range", value = "price"),
                    @ExampleObject(name = "Discount", value = "discount"),
                    @ExampleObject(name = "Category", value = "category")
            })
            @Valid @RequestParam String paramName,
            @Parameter(description = "Criteria value (for price use format: minPrice-maxPrice, e.g., 100-300)", required = true, examples = {
                    @ExampleObject(name = "Price range", value = "100-300"),
                    @ExampleObject(name = "Category name", value = "electronics"),
                    @ExampleObject(name = "Product name part", value = "phone")
            })
            @Valid @RequestParam String paramValue,
            @Parameter(description = "Sort direction: asc (ascending) or desc (descending)", required = true, examples = {
                    @ExampleObject(name = "Ascending", value = "asc"),
                    @ExampleObject(name = "Descending", value = "desc")
            })
            @Valid @RequestParam String sortDirection,
            @Parameter(description = "Page number (0-based)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "20") @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortDirection), getSortProperty(paramName)));
        return ResponseEntity.ok(productService.getProductsByCriteria(paramName, paramValue, pageable));
    }

    /**
     * Retrieves a paginated list of all products with user-friendly details,
     * excluding internal IDs and timestamps.
     *
     * @param page the page number (0-based) to retrieve, default is 0
     * @param size the number of products per page, default is 20
     * @param sort in the field by which the result will be sorted, the default is "name"
     * @param direction in the direction of sorting, either "asc" for ascending or "desc" for descending, default is "asc"
     * @return a ResponseEntity containing a Page of ProductResponseForUserDto instances
     */
    @Operation(summary = "Get all products for users",
            description = "Retrieves a list of all products with user-friendly details (without internal IDs and timestamps).")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Products retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ProductResponseForUserDto.class)))
    })
    @GetMapping("/getAllProductForUser")
    public ResponseEntity<Page<ProductResponseForUserDto>> getAllProductForUser(
            @Parameter(description = "Page number (0-based)", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "20") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "name") String sort,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "asc") String direction) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(direction), sort));
        return ResponseEntity.ok(productService.getProductsForUser(pageable));
    }

    /**
     * Retrieves the top five products with the highest discounts available for the current day.
     *
     * @return a ResponseEntity containing a list of ProductResponseForUserDto objects representing
     *         the top five discounted products of the day, or an empty list if no discounted products are available.
     */
    @Operation(summary = "Get top five discounted products",
            description = "Retrieves the top five products with the highest discounts available for the current day.")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Products retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ProductResponseForUserDto.class))),
            @ApiResponse(responseCode = "404",
                    description = "No discounted products found")
    })
    @GetMapping("/getTopFiveProducts")
    public ResponseEntity<List<ProductResponseForUserDto>> getTopFiveDiscountedProductsOfTheDay() {
        return ResponseEntity.ok(productService.getTopFiveDiscountedProductsOfTheDay());
    }

    /**
     * Determines the sort property based on the given parameter name.
     * Maps specific parameter names to their corresponding properties to be used for sorting.
     *
     * @param paramName the name of the parameter based on which the sort property is to be determined
     * @return the corresponding sort property as a string; defaults to "id" if no match is found
     */
    private String getSortProperty(String paramName) {
        return switch (paramName) {
            case "price" -> "price";
            case "discount" -> "discountPrice";
            case "category" -> "category.categoryName";
            case "name" -> "name";
            case "createDate" -> "createdAt";
            default -> "id";
        };
    }
}