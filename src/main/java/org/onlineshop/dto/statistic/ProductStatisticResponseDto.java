package org.onlineshop.dto.statistic;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ProductStatisticResponseDto {

    private String productName;

    private String productCategory;

    private BigDecimal productPrice;

    private BigDecimal productDiscountPrice;

    private  Integer productQuantity;

}
