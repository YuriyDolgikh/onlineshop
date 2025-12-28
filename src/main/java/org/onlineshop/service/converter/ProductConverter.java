package org.onlineshop.service.converter;

import lombok.Generated;
import lombok.RequiredArgsConstructor;
import org.onlineshop.dto.product.ProductRequestDto;
import org.onlineshop.dto.product.ProductResponseDto;
import org.onlineshop.dto.product.ProductResponseForUserDto;
import org.onlineshop.dto.statistic.ProductStatisticResponseDto;
import org.onlineshop.entity.Category;
import org.onlineshop.entity.Product;
import org.onlineshop.service.CategoryService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Generated
@Service
@RequiredArgsConstructor
public class ProductConverter {
    private final CategoryService categoryService;

    public Product fromDto(ProductRequestDto productRequestDto) {
        Category category = categoryService.getCategoryByName(productRequestDto.getProductCategory());

        return Product.builder()
                .name(productRequestDto.getProductName())
                .description(productRequestDto.getProductDescription())
                .price(productRequestDto.getProductPrice())
                .category(category)
                .discountPrice(productRequestDto.getProductDiscountPrice())
                .image(productRequestDto.getImage())
                .build();
    }

    public ProductResponseDto toDto(Product product) {
        return ProductResponseDto.builder()
                .productId(product.getId())
                .productName(product.getName())
                .productDescription(product.getDescription())
                .productCategory(product.getCategory().getCategoryName())
                .productPrice(product.getPrice())
                .productDiscountPrice(product.getDiscountPrice() != null
                        ? product.getDiscountPrice()
                        : BigDecimal.ZERO)
                .image(product.getImage())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }

    public List<ProductResponseDto> toDtos(List<Product> products) {
        return products.stream()
                .map(this::toDto)
                .toList();
    }

    public ProductResponseForUserDto toUserDto(ProductResponseDto productResponseDto) {
        return ProductResponseForUserDto.builder()

                .productName(productResponseDto.getProductName())
                .productDescription(productResponseDto.getProductDescription())
                .productCategory(productResponseDto.getProductCategory())
                .productPrice(productResponseDto.getProductPrice())
                .productDiscountPrice(productResponseDto.getProductDiscountPrice())
                .image(productResponseDto.getImage())
                .build();
    }

    public List<ProductResponseForUserDto> toUserDtos(List<ProductResponseDto> products) {
        return products.stream()
                .map(this::toUserDto)
                .toList();
    }

    public List<ProductStatisticResponseDto> fromMapToList(Map<Product, Integer> statistic) {
        List<ProductStatisticResponseDto> response = new ArrayList<>();
        statistic.forEach((key, value) -> {
            ProductStatisticResponseDto item = ProductStatisticResponseDto.builder()
                    .productName(key.getName())
                    .productCategory(key.getCategory().getCategoryName())
                    .productPrice(key.getPrice())
                    .productDiscountPrice(key.getDiscountPrice())
                    .productQuantity(value)
                    .build();

            response.add(item);
        });
        return response;
    }
}
