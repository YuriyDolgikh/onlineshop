package org.onlineshop.service.converter;

import org.onlineshop.dto.cartItem.CartItemResponseDto;
import org.onlineshop.entity.CartItem;
import org.onlineshop.entity.OrderItem;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CartItemConverter {
    public CartItemResponseDto toDto(CartItem cartItem) {
        return CartItemResponseDto.builder()
                .product(cartItem.getProduct())
                .quantity(cartItem.getQuantity())
                .build();
    }

    public Set<CartItemResponseDto> toDtos(Set<CartItem> cartItems) {
        return cartItems.stream()
                .map(this::toDto)
                .collect(Collectors.toSet());
    }

    public OrderItem cartItemToOrderItem(CartItem cartItem) {
        BigDecimal price = cartItem.getProduct().getPrice();
        BigDecimal discount = price.multiply(cartItem.getProduct().getDiscountPrice()).divide(new BigDecimal(100));
        return OrderItem.builder()
                .product(cartItem.getProduct())
                .quantity(cartItem.getQuantity())
                .priceAtPurchase(price.subtract(discount))
                .build();
    }
}
