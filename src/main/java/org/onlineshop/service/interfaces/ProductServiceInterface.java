package org.onlineshop.service.interfaces;

import org.onlineshop.dto.product.ProductRequestDto;
import org.onlineshop.dto.product.ProductResponseDto;
import org.onlineshop.dto.product.ProductUpdateDto;
import org.onlineshop.entity.Product;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ProductServiceInterface {
    ProductResponseDto addProduct(ProductRequestDto productRequestDto);

    ProductResponseDto updateProduct(Integer productId, ProductUpdateDto productRequestDto);

    ProductResponseDto deleteProduct(Integer productId);

    List<ProductResponseDto> getProductsByPartOfNameIgnoreCase(String partOfName, String sortDirection);

    List<ProductResponseDto> getProductsByCategory(String categoryNane, String sortDirection);

    List<ProductResponseDto> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice, String sortDirection);

    List<ProductResponseDto> getProductsByDiscount(String sortDirection);

    List<ProductResponseDto> getProductsByCreateDate(String sortDirection);

    List<ProductResponseDto> getAllProducts();

    List<ProductResponseDto> getProductsByCriteria(String paramName, String paramValue, String sortDirection);

    Optional<Product> getProductById(Integer productId);
}
