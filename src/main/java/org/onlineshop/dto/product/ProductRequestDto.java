package org.onlineshop.dto.product;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class        ProductRequestDto {

    @Size(min = 3, max = 20, message = "Product title must be between 3 and 20 characters")
    private String productName;

    @Size(min = 3, max = 100, message = "Product description must be between 3 and 100 characters")
    private String productDescription;

    private String productCategory;

    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    private BigDecimal productPrice;

    private BigDecimal productDiscountPrice;

    private String image;

}
