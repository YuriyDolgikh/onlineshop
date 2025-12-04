package org.onlineshop.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.onlineshop.dto.cartItem.*;
import org.onlineshop.service.CartItemService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/cartItems")
@Tag(name = "Cart Items Management", description = "APIs for managing individual items in the user's shopping cart")
public class CartItemController {

    private final CartItemService cartItemService;

    /**
     * Adds a new item to the current user's cart.
     *
     * @param cartItemRequestDto the details of the item to be added to the cart
     * @return a response entity with HTTP status 201 (CREATED) to indicate the item was added successfully.
     */
    @Operation(
            summary = "Add item to cart",
            description = "Adds a new product item to the current user's shopping cart. If the product already exists in cart, increases the quantity."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Item successfully added to cart",
                    content = @Content(schema = @Schema(implementation = CartItemResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request - invalid input data or product not found"
            )
    })
    @PostMapping
    public ResponseEntity<CartItemSimpleResponseDto> createCartItem(
            @Parameter(description = "Cart item creation data", required = true)
            @Valid @RequestBody CartItemRequestDto cartItemRequestDto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(cartItemService.addItemToCart(cartItemRequestDto));
    }

    /**
     * Deletes an item from the current user's cart.
     *
     * @param productId the ID of the item to be deleted from the cart
     * @return a response entity with HTTP status 200 (OK) to indicate the item was deleted successfully.
     */
    @Operation(
            summary = "Remove item from cart",
            description = "Removes a specific product item from the current user's shopping cart."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Item successfully removed from cart",
                    content = @Content(schema = @Schema(implementation = CartItemResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not found - product not found in cart"
            )
    })
    @DeleteMapping("/{productId}")
    public ResponseEntity<CartItemResponseDto> deleteCartItem(
            @Parameter(
                    description = "ID of the product to remove from cart",
                    required = true,
                    example = "123"
            )
            @Valid @PathVariable Integer productId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(cartItemService.removeItemFromCart(productId));
    }

    /**
     * Updates an existing item in the user's cart with the provided details.
     *
     * @param cartItemUpdateDto the details of the cart item to be updated, including the product ID and the new quantity
     * @return a response entity containing the updated cart item details and an HTTP status of 200 (OK)
     */
    @Operation(
            summary = "Update cart item quantity",
            description = "Updates the quantity of a specific product item in the current user's shopping cart."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Item quantity successfully updated",
                    content = @Content(schema = @Schema(implementation = CartItemResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request - invalid quantity or product not found"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not found - product not found in cart"
            )
    })
    @PutMapping
    public ResponseEntity<CartItemResponseDto> updateCartItem(
            @Parameter(description = "Cart item update data", required = true)
            @Valid @RequestBody CartItemUpdateDto cartItemUpdateDto) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(cartItemService.updateItemInCart(cartItemUpdateDto));
    }

    /**
     * Retrieves all items in the current user's cart.
     *
     * @return a response entity containing a set of cart item response DTOs and an HTTP status of 200 (OK)
     */
    @Operation(
            summary = "Get all cart items",
            description = "Retrieves all items currently in the user's shopping cart."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Cart items retrieved successfully",
                    content = @Content(schema = @Schema(implementation = CartItemResponseDto.class))
            )
    })
    @GetMapping
    public ResponseEntity<Set<CartItemFullResponseDto>> getCartItems() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(cartItemService.getCartItems());
    }
}