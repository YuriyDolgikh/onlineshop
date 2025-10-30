package org.onlineshop.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.onlineshop.dto.product.ProductRequestDto;
import org.onlineshop.dto.product.ProductResponseDto;
import org.onlineshop.dto.product.ProductResponseDtoForUser;
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
public class ProductController {

    private final ProductService productService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_ADMIN')")
    public ResponseEntity<ProductResponseDto> addNewProduct(@Valid @RequestBody ProductRequestDto requestDto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(productService.addProduct(requestDto));

    }

    @PutMapping
    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_ADMIN')")
    public ResponseEntity<ProductResponseDto> updateProduct(@Valid @RequestBody Integer productId, @RequestBody ProductRequestDto requestDto) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(productService.updateProduct(productId, requestDto));
    }

    @DeleteMapping("{productId}")
    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_ADMIN')")
    public ResponseEntity<ProductResponseDto> deleteProduct(@Valid @PathVariable Integer productId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(productService.deleteProduct(productId));
    }

    @PutMapping("/updateProductDiscount/{productId}/{newDiscount}")
    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_ADMIN')")
    public ResponseEntity<ProductResponseDto> updateProductDiscount( @Valid @PathVariable Integer productId , @Valid @PathVariable BigDecimal newDiscount) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(productService.setDiscountPrice(productId , newDiscount));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_ADMIN')")
    public ResponseEntity<List<ProductResponseDto>> getAllProductsForAdmin() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(productService.getAllProducts());
    }

    @GetMapping("/getProductByPartOfName")
    public ResponseEntity<List<ProductResponseDto>> getProductByPartOfNameOfIgnoreCase(@Valid @RequestBody String partOfName, @Valid @RequestBody String sortDirection) {
        return ResponseEntity
                .status(HttpStatus.FOUND)
                .body(productService.getProductsByPartOfNameIgnoreCase(partOfName, sortDirection));
    }

    @GetMapping("/getProductsByCriteria")
    public ResponseEntity<List<ProductResponseDto>> getProductsByCriteria(@Valid @RequestBody String paramName, @Valid @RequestBody String paramValue, @Valid @RequestBody String sortDirection) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(productService.getProductsByCriteria(paramName, paramValue, sortDirection));
    }

    @GetMapping("/getProductByCategory")
    public ResponseEntity<List<ProductResponseDto>> getProductByCategory(@Valid @RequestBody String categoryName, @Valid @RequestBody String sortDirection) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(productService.getProductsByCategory(categoryName, sortDirection));
    }

    @GetMapping("/getProductsByPriceRange")
    public ResponseEntity<List<ProductResponseDto>> getProductsByPriceRange(@Valid @RequestBody BigDecimal minPrice, @Valid @RequestBody BigDecimal maxPrice, @Valid @RequestBody String sortDirection) {
        return ResponseEntity
                .status(HttpStatus.FOUND)
                .body(productService.getProductsByPriceRange(minPrice, maxPrice, sortDirection));
    }

    @GetMapping("/getProductsByCreateDate")
    public ResponseEntity<List<ProductResponseDto>> getProductsByCreateDate(@Valid @RequestBody String sortDirection) {
        return ResponseEntity
                .status(HttpStatus.FOUND)
                .body(productService.getProductsByCreateDate(sortDirection));
    }

    @GetMapping("/getProductsByDiscount")
    public ResponseEntity<List<ProductResponseDto>> getProductsByDiscount(@Valid @RequestBody String sortDirection) {
        return ResponseEntity
                .status(HttpStatus.FOUND)
                .body(productService.getProductsByDiscount(sortDirection));
    }

    @GetMapping("/getAllProductForUser")
    public ResponseEntity<List<ProductResponseDtoForUser>> getAllProductForUser() {
        return ResponseEntity
                .status(HttpStatus.FOUND)
                .body(productService.getAllProductsForUser());
    }

    @GetMapping("/getTopFiveDiscountedProductsOfTheDay")
    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_ADMIN')")
    public ResponseEntity<List<ProductResponseDto>> getTopFiveDiscountedProductsOfTheDay() {
        return ResponseEntity
                .status(HttpStatus.FOUND)
                .body(productService.getTopFiveDiscountedProductsOfTheDay());
    }

    @GetMapping("/getTopFiveDiscountedProductsOfTheDayForUser")
    public ResponseEntity<List<ProductResponseDtoForUser>> getTopFiveDiscountedProductsOfTheDayForUser() {
        return ResponseEntity
                .status(HttpStatus.FOUND)
                .body(productService.getTopFiveDiscountedProductsOfTheDayForUser());
    }

}
