package org.onlineshop.controller;

import lombok.RequiredArgsConstructor;
import org.onlineshop.dto.favourite.FavouriteResponseDto;
import org.onlineshop.service.FavouriteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("v1/favorites")
public class FavouriteController {
    private final FavouriteService favouriteService;

    @GetMapping
    public ResponseEntity<List<FavouriteResponseDto>> getFavorites() {
        return ResponseEntity
                .status(HttpStatus.FOUND)
                .body(favouriteService.getFavourites());
    }

    @PostMapping("/{productId}")
    public ResponseEntity<FavouriteResponseDto> addFavourite(@PathVariable Integer productId) {
        FavouriteResponseDto response = favouriteService.addFavourite(productId);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<FavouriteResponseDto> deleteFavourite(@PathVariable Integer productId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(favouriteService.deleteFavourite(productId));
    }
}
