package org.onlineshop.dto.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CategoryRequestDto {

    @NotNull(message = "Category name must by not Null")
    @NotBlank(message = "Category name must by not Blank")
    @Size(min = 3, max = 20, message = "Category name must be between 3 and 20 characters")
    private String categoryName;

    private String image;

}
