package org.onlineshop.service.interfaces;

import org.onlineshop.dto.product.ProductRequestDto;
import org.onlineshop.dto.product.ProductResponseDto;
import org.onlineshop.dto.product.ProductUpdateDto;
import org.onlineshop.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Optional;

public interface ProductServiceInterface {
    ProductResponseDto addProduct(ProductRequestDto productRequestDto);

    ProductResponseDto updateProduct(Integer productId, ProductUpdateDto productRequestDto);

    ProductResponseDto deleteProduct(Integer productId);

    Page<ProductResponseDto> getProductsByPartOfNameIgnoreCase(String partOfName, Pageable pageable);

    Page<ProductResponseDto> getProductsByCategory(String categoryName, Pageable pageable);

    Page<ProductResponseDto> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);

    Page<ProductResponseDto> getProductsByDiscount(Pageable pageable);

    Page<ProductResponseDto> getProductsByCreateDate(Pageable pageable);

    Page<ProductResponseDto> getAllProducts(Pageable pageable);

    Page<ProductResponseDto> getProductsByCriteria(String paramName, String paramValue, Pageable pageable);

    Optional<Product> getProductById(Integer productId);
}