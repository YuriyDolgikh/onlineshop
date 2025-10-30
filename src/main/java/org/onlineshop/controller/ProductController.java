package org.onlineshop.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.onlineshop.dto.product.ProductRequestDto;
import org.onlineshop.dto.product.ProductResponseDto;
import org.onlineshop.dto.product.ProductResponseForUserDto;
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

    @GetMapping("/getProductsByCriteria")
    public ResponseEntity<List<ProductResponseDto>> getProductsByCriteria(@Valid @RequestBody String paramName, @Valid @RequestBody String paramValue, @Valid @RequestBody String sortDirection) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(productService.getProductsByCriteria(paramName, paramValue, sortDirection));
    }

    @GetMapping("/getAllProductForUser")
    public ResponseEntity<List<ProductResponseForUserDto>> getAllProductForUser() {
        return ResponseEntity
                .status(HttpStatus.FOUND)
                .body(productService.getAllProductsForUser());
    }

    @GetMapping("/getTopFiveProducts")
    public ResponseEntity<List<ProductResponseForUserDto>> getTopFiveDiscountedProductsOfTheDay() {
        return ResponseEntity
                .status(HttpStatus.FOUND)
                .body(productService.getTopFiveDiscountedProductsOfTheDay());
    }

}
