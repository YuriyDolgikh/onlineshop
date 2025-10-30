package org.onlineshop.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.onlineshop.dto.cartItem.CartItemResponseDto;
import org.onlineshop.service.CartItemService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/cartItems")
public class CartItemController {

    private final CartItemService cartItemService;

    @PostMapping
    public ResponseEntity<CartItemResponseDto> createCartItem(@Valid @RequestBody Integer productId, @Valid @RequestBody Integer quantity) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(cartItemService.addItemToCart(productId, quantity));
    }

    @DeleteMapping
    public ResponseEntity<CartItemResponseDto> deleteCartItem(@Valid @RequestBody Integer productId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(cartItemService.removeItemFromCart(productId));
    }

    @PutMapping
    public ResponseEntity<CartItemResponseDto> updateCartItem(@Valid @RequestBody Integer productId, @Valid @RequestBody Integer quantity) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(cartItemService.updateItemInCart(productId, quantity));
    }

    @GetMapping
    public ResponseEntity<List<CartItemResponseDto>> getCartItem(@Valid @RequestBody Integer productId) {
        return ResponseEntity
                .status(HttpStatus.FOUND)
                .body(cartItemService.getCartItems());
    }

}
