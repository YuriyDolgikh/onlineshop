package org.onlineshop.service.interfaces;

import org.onlineshop.dto.cartItem.CartItemRequestDto;
import org.onlineshop.dto.cartItem.CartItemResponseDto;
import org.onlineshop.dto.cartItem.CartItemUpdateDto;

import java.util.Set;

public interface CartItemServiceInterface {
    CartItemResponseDto addItemToCart(CartItemRequestDto cartItemRequestDto);

    CartItemResponseDto removeItemFromCart(Integer productId);

    CartItemResponseDto updateItemInCart(CartItemUpdateDto cartItemUpdateDto);

    Set<CartItemResponseDto> getCartItems();
}
