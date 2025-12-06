package org.onlineshop.service.interfaces;

import org.onlineshop.dto.favourite.FavouriteResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FavouriteServiceInterface {
    FavouriteResponseDto addFavourite(Integer productId);

    FavouriteResponseDto deleteFavourite(Integer productId);

    Page<FavouriteResponseDto> getFavourites(Pageable pageable);
}
