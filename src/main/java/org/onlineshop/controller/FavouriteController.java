package org.onlineshop.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Favorites Management", description = "APIs for managing user favorite products")
public class FavouriteController {
    private final FavouriteService favouriteService;

    /**
     * Retrieves a list of favorite items for the current user.
     *
     * @return a ResponseEntity containing a List of FavouriteResponseDto objects,
     *         representing the user's favorite items, with an HTTP status of OK.
     */
    @Operation(
            summary = "Get user favorites",
            description = "Retrieves a list of all favorite products for the currently authenticated user."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Favorites retrieved successfully",
                    content = @Content(schema = @Schema(implementation = FavouriteResponseDto.class))
            )
    })
    @GetMapping
    public ResponseEntity<List<FavouriteResponseDto>> getFavorites() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(favouriteService.getFavourites());
    }

    /**
     * Adds a product to the current user's favorites.
     *
     * @param productId the ID of the product to be added to the user's favorites list
     * @return a ResponseEntity containing a FavouriteResponseDto object,
     * representing the newly added product, with an HTTP status of CREATED.
     */
    @Operation(
            summary = "Add product to favorites",
            description = "Adds a product to the current user's favorites list. If the product is already in favorites, returns an error."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Product successfully added to favorites",
                    content = @Content(schema = @Schema(implementation = FavouriteResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request - product already in favorites or invalid product ID"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not found - product not found"
            )
    })
    @PostMapping("/{productId}")
    public ResponseEntity<FavouriteResponseDto> addFavourite(
            @Parameter(
                    description = "ID of the product to add to favorites",
                    required = true,
                    example = "123"
            )
            @PathVariable Integer productId) {
        FavouriteResponseDto response = favouriteService.addFavourite(productId);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    /**
     * Removes a product from the current user's favorites.
     *
     * @param productId the ID of the product to be removed from the user's favorites list
     * @return a ResponseEntity containing a FavouriteResponseDto of the removed product, with an HTTP status of OK.
     */
    @Operation(
            summary = "Remove product from favorites",
            description = "Removes a product from the current user's favorites list."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Product successfully removed from favorites",
                    content = @Content(schema = @Schema(implementation = FavouriteResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not found - product not found in favorites"
            )
    })
    @DeleteMapping("/{productId}")
    public ResponseEntity<FavouriteResponseDto> deleteFavourite(
            @Parameter(
                    description = "ID of the product to remove from favorites",
                    required = true,
                    example = "123"
            )
            @PathVariable Integer productId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(favouriteService.deleteFavourite(productId));
    }
}