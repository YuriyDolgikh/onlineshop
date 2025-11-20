package org.onlineshop.service.converter;

import lombok.Generated;
import lombok.RequiredArgsConstructor;
import org.onlineshop.dto.statistic.ProductStatisticResponseDto;
import org.onlineshop.entity.Product;
import org.springframework.stereotype.Service;

import java.util.List;

@Generated
@Service
@RequiredArgsConstructor
public class StatisticConverter {
    public static ProductStatisticResponseDto toDto(Product product, Integer quantity) {
        if (product == null) {
            throw new IllegalArgumentException("Product must not be null");
        }
        if (quantity == null || quantity < 0) {
            quantity = 0; // можно установить 0 по умолчанию
        }

        return ProductStatisticResponseDto.builder()
                .productName(product.getName())
                .productCategory(product.getCategory().categoryName)
                .productPrice(product.getPrice())
                .productDiscountPrice(product.getDiscountPrice())
                .productQuantity(quantity)
                .build();
    }

}
