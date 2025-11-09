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

    @PostMapping
    public ResponseEntity<CartItemResponseDto> createCartItem(@Valid @RequestBody CartItemRequestDto cartItemRequestDto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(cartItemService.addItemToCart(cartItemRequestDto));
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<CartItemResponseDto> deleteCartItem(@Valid @PathVariable Integer productId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(cartItemService.removeItemFromCart(productId));
    }

    @PutMapping
    public ResponseEntity<CartItemResponseDto> updateCartItem(@Valid @RequestBody CartItemUpdateDto cartItemUpdateDto) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(cartItemService.updateItemInCart(cartItemUpdateDto));
    }

    @GetMapping("/{productId}")
    public ResponseEntity<Set<CartItemResponseDto>> getCartItem(@Valid @PathVariable Integer productId) {
        return ResponseEntity
                .status(HttpStatus.FOUND)
                .body(cartItemService.getCartItems());
    }

}
