package org.onlineshop.dto.product;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ProductUpdateDto {

    @Pattern(regexp = "^$|^.{3,20}$", message = "Product name must be empty or between 3 and 20 characters")
    private String productName;

    @Size(max = 500, message = "Product description must be less than 500 characters")
    private String productDescription;

    @Pattern(regexp = "^$|^.{3,20}$", message = "Category name must be empty or between 3 and 20 characters")
    private String productCategory;

    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    private BigDecimal productPrice;

    @DecimalMin(value = "0", message = "Discount price cannot be negative")
    @DecimalMax(value = "100", message = "Discount price cannot be greater than 100")
    private BigDecimal productDiscountPrice;

    @Size(max = 256, message = "Image URL must be less than 256 characters")
    private String image;

}
