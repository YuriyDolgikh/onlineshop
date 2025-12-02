package org.onlineshop.dto.product;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ProductResponseForUserDto {

    private String productName;

    private String productDescription;

    private String productCategory;

    private BigDecimal productPrice;

    private BigDecimal productDiscountPrice;

    private String image;
}
