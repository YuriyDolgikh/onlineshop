package org.onlineshop.dto.favourite;

import lombok.*;

import java.util.Objects;

@Data
@Generated
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FavouriteResponseDto {

    private Integer favouriteId;

    private String productName;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FavouriteResponseDto)) return false;
        FavouriteResponseDto that = (FavouriteResponseDto) o;
        return Objects.equals(favouriteId, that.favouriteId) &&
                Objects.equals(productName, that.productName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(favouriteId, productName);
    }
}
