package org.onlineshop.service;

import org.onlineshop.dto.product.ProductRequestDto;
import org.onlineshop.dto.product.ProductResponseDto;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface ProductServiceInterface {
    ProductResponseDto addProduct(ProductRequestDto productRequestDto);
    ProductResponseDto updateProduct(Integer productId, ProductRequestDto productRequestDto);
    ProductResponseDto deleteProduct(Integer productId);
    ProductResponseDto getProductById(Integer productId);
    List<ProductResponseDto> getProductsByPartOfNameIgnoreCase(String partOfName, Sort sort);
    List<ProductResponseDto> getProductsByCategory(Integer categoryId, Sort sort);
    List<ProductResponseDto> getProductsByPriceRange(Double minPrice, Double maxPrice, Sort sort);
    List<ProductResponseDto> getProductsByDiscount(Sort sort);
    List<ProductResponseDto> getProductsByCreateDate(Sort sort);
    List<ProductResponseDto> getAllProducts();
}
