package org.onlineshop.service.converter;

import lombok.RequiredArgsConstructor;
import org.onlineshop.dto.favourite.FavouriteResponseDto;
import org.onlineshop.entity.Favourite;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FavouriteConverter {
    public FavouriteResponseDto toDto(Favourite favourite) {
        if (favourite == null) {
            throw new IllegalArgumentException("Favourite cannot be null");
        }
        return FavouriteResponseDto.builder()
                .favouriteId(favourite.getFavouriteId())
                .productName(favourite.getProduct().getName())
                .build();
    }

    public List<FavouriteResponseDto> toDtos(List<Favourite> favourites){
        return favourites.stream()
                .map(this::toDto)
                .toList();
    }
}
