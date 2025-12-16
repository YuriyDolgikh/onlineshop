package org.onlineshop.dto.favourite;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
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
