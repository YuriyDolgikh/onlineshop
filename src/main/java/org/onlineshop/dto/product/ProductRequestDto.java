package org.onlineshop.dto.product;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ProductRequestDto {

    @NotBlank(message = "Product title is required and must be not blank")
    @Size(min = 3, max = 20, message = "Product title must be between 3 and 20 characters")
    private String productName;

    @Size(max = 500, message = "Product description must be less than 500 characters")
    private String productDescription;

    @NotBlank(message = "Product category is required and must be not blank")
    private String productCategory;

    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    @NotNull(message = "Price cannot be null")
    private BigDecimal productPrice;

    @NotNull(message = "Discount price cannot be null")
    @DecimalMin(value = "0", message = "Discount price cannot be negative")
    private BigDecimal productDiscountPrice;

    @NotBlank(message = "Image URL cannot be null or Blank")
    @Size(max = 256, message = "Image URL must be less than 256 characters")
    private String image;
}