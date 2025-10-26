package org.onlineshop.service;

import org.onlineshop.dto.favourite.FavouriteResponseDto;

import java.util.List;

public interface FavouriteServiceInterface {
    FavouriteResponseDto addFavourite(Integer productId);
    FavouriteResponseDto deleteFavourite(Integer productId);
    List<FavouriteResponseDto> getFavourites();
}
