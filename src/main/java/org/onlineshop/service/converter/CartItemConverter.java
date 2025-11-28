package org.onlineshop.service.converter;

import lombok.Generated;
import org.onlineshop.dto.cartItem.CartItemFullResponseDto;
import org.onlineshop.dto.cartItem.CartItemResponseDto;
import org.onlineshop.dto.cartItem.CartItemSympleResponseDto;
import org.onlineshop.entity.CartItem;
import org.onlineshop.entity.OrderItem;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Set;
import java.util.stream.Collectors;

@Generated
@Service
public class CartItemConverter {
    public CartItemResponseDto toDto(CartItem cartItem) {
        return CartItemResponseDto.builder()
                .product(cartItem.getProduct())
                .quantity(cartItem.getQuantity())
                .build();
    }

    public CartItemFullResponseDto toFullDto(CartItem cartItem) {
        return CartItemFullResponseDto.builder()
                .cartItemId(cartItem.getCartItemId())
                .productName(cartItem.getProduct().getName())
                .categoryName(cartItem.getProduct().getCategory().getCategoryName())
                .productPrice(cartItem.getProduct().getPrice().doubleValue())
                .productDiscountPrice(cartItem.getProduct().getDiscountPrice().doubleValue())
                .quantity(cartItem.getQuantity())
                .build();
    }

    public Set<CartItemFullResponseDto> toFullDtos(Set<CartItem> cartItems) {
        return cartItems.stream()
                .map(this::toFullDto)
                .collect(Collectors.toSet());
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
                .priceAtPurchase(price.subtract(discount).setScale(2, RoundingMode.CEILING))
                .build();
    }

    public CartItemSympleResponseDto toSympleDto(CartItem cartItem) {
        return CartItemSympleResponseDto.builder()
                .productName(cartItem.getProduct().getName())
                .quantity(cartItem.getQuantity())
                .build();
    }

    public CartItemSympleResponseDto toSympleDtoFromDto(CartItemResponseDto cartItemResponseDto) {
        return CartItemSympleResponseDto.builder()
                .productName(cartItemResponseDto.getProduct().getName())
                .quantity(cartItemResponseDto.getQuantity())
                .build();
    }
}
