package org.onlineshop.service.interfaces;

import org.onlineshop.dto.cartItem.*;

import java.util.Set;

public interface CartItemServiceInterface {
    CartItemSimpleResponseDto addItemToCart(CartItemRequestDto cartItemRequestDto);

    CartItemResponseDto removeItemFromCart(Integer productId);

    CartItemResponseDto updateItemInCart(CartItemUpdateDto cartItemUpdateDto);

    Set<CartItemFullResponseDto> getCartItems();
}
