package org.onlineshop.dto.product;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class ProductResponseDto {

    private Integer productId;

    private String productName;

    private String productDescription;

    private String productCategory;

    private BigDecimal productPrice;

    private BigDecimal productDiscountPrice;

    private String productImageUrl;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
