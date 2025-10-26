package org.onlineshop.dto.favourite;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FavouriteResponseDto {

    private Integer favouriteId;

    private String productName;
}
