package org.onlineshop.service.interfaces;

import org.onlineshop.dto.cart.CartResponseDto;
import org.onlineshop.dto.cartItem.CartItemSympleResponseDto;
import org.onlineshop.entity.Cart;

public interface CartServiceInterface {
    void clearCart();

    void transferCartToOrder();

    CartItemSympleResponseDto getCartFullData();

    Cart saveCart(Cart cart);
}
