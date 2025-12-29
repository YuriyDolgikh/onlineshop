package org.onlineshop.dto.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CategoryRequestDto {

    @NotBlank(message = "Category name must by not Blank or Null")
    @Size(min = 3, max = 20, message = "Category name must be between 3 and 20 characters")
    private String categoryName;

    @Size(max = 256, message = "Image URL must be less than 256 characters")
    private String image;
}