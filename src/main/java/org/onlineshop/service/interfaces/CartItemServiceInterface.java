package org.onlineshop.service.interfaces;

import org.onlineshop.dto.cartItem.CartItemResponseDto;

import java.util.List;

public interface CartItemServiceInterface {
    CartItemResponseDto addItemToCart(Integer productId, Integer quantity);
    CartItemResponseDto removeItemFromCart(Integer productId);
    CartItemResponseDto updateItemInCart(Integer productId, Integer quantity);
    List<CartItemResponseDto> getCartItems();
}
