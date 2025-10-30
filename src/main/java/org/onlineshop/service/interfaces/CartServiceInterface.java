package org.onlineshop.service.interfaces;

import org.onlineshop.dto.cart.CartResponseDto;

public interface CartServiceInterface {
    void clearCart();
    void transferToOrder();
    CartResponseDto getCartFullData();
}
