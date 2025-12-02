package org.onlineshop.dto.product;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ProductUpdateDto {

    @Size(min = 3, max = 20, message = "Product name must be between 3 and 20 characters")
    private String productName;

    @Size(max = 500, message = "Product description must be less than 500 characters")
    private String productDescription;

    @Size(min = 3, max = 20, message = "Product category must be between 3 and 20 characters")
    private String productCategory;

    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    private BigDecimal productPrice;

    @DecimalMin(value = "0", message = "Discount price cannot be negative")
    private BigDecimal productDiscountPrice;

    @Size(max = 256, message = "Image URL must be less than 256 characters")
    private String image;

}
