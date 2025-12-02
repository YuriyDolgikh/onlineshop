package org.onlineshop.dto.category;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CategoryUpdateDto {

    @Size(min = 3, max = 20, message = "Category name must be between 3 and 20 characters")
    private String categoryName;

    @Size(max = 256, message = "Image URL must be less than 256 characters")
    private String image;
}
