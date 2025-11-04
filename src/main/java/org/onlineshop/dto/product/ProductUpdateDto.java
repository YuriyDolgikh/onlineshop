package org.onlineshop.dto.product;

import jakarta.validation.constraints.DecimalMin;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ProductUpdateDto {

    private String productName;

    private String productDescription;

    private String productCategory;

    private BigDecimal productPrice;

    private BigDecimal productDiscountPrice;

    private String image;

}
