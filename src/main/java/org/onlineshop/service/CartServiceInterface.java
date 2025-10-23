package org.onlineshop.service;

import org.onlineshop.dto.cart.CartResponseDto;

public interface CartServiceInterface {
    void clearCart();
    void transferToOrder();
    CartResponseDto getCartFullData(Integer userId);
}
