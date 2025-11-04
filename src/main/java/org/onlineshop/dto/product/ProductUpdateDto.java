package org.onlineshop.dto.product;

import jakarta.validation.constraints.DecimalMin;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import java.math.BigDecimal;

@Data
@Builder
public class ProductUpdateDto {

    private String productName;

    private String productDescription;

    private String productCategory;

    private BigDecimal productPrice;

    private BigDecimal productDiscountPrice;

    @URL(message = "Invalid image URL")
    private String image;

}
