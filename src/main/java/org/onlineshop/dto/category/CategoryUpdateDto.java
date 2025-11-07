package org.onlineshop.dto.category;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CategoryUpdateDto {

    private String categoryName;

    @URL(message = "Invalid image URL")
    private String image;
}
