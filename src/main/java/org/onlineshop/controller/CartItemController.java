package org.onlineshop.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.onlineshop.dto.cartItem.CartItemRequestDto;
import org.onlineshop.dto.cartItem.CartItemResponseDto;
import org.onlineshop.dto.cartItem.CartItemUpdateDto;
import org.onlineshop.service.CartItemService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/cartItems")
public class CartItemController {

    private final CartItemService cartItemService;

    /**
     * Adds a new item to the current user's cart.
     *
     * @param cartItemRequestDto the details of the item to be added to the cart
     * @return a response entity with HTTP status 201 (CREATED) to indicate the item was added successfully.
     */
    @PostMapping
    public ResponseEntity<CartItemResponseDto> createCartItem(@Valid @RequestBody CartItemRequestDto cartItemRequestDto) {
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
    @DeleteMapping("/{productId}")
    public ResponseEntity<CartItemResponseDto> deleteCartItem(@Valid @PathVariable Integer productId) {
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
    @PutMapping
    public ResponseEntity<CartItemResponseDto> updateCartItem(@Valid @RequestBody CartItemUpdateDto cartItemUpdateDto) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(cartItemService.updateItemInCart(cartItemUpdateDto));
    }

    /**
     * Retrieves all items from the current user's cart.
     *
     * @return a response entity containing a set of cart item response DTOs and an HTTP status of 302 (FOUND)
     */
    @GetMapping
    public ResponseEntity<Set<CartItemResponseDto>> getCartItems() {
        return ResponseEntity
                .status(HttpStatus.FOUND)
                .body(cartItemService.getCartItems());
    }
}
