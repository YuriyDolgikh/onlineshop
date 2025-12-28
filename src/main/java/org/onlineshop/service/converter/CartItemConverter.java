package org.onlineshop.service.converter;

import lombok.Generated;
import org.onlineshop.dto.cartItem.CartItemFullResponseDto;
import org.onlineshop.dto.cartItem.CartItemResponseDto;
import org.onlineshop.dto.cartItem.CartItemSimpleResponseDto;
import org.onlineshop.entity.CartItem;
import org.onlineshop.entity.OrderItem;
import org.onlineshop.entity.Product;
import org.onlineshop.service.util.PriceCalculator;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Set;
import java.util.stream.Collectors;

@Generated
@Service
public class CartItemConverter {
    private final PriceCalculator priceCalculator;

    public CartItemConverter(PriceCalculator priceCalculator) {
        this.priceCalculator = priceCalculator;
    }

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
                .productPrice(cartItem.getProduct().getPrice().setScale(2, RoundingMode.HALF_UP))
                .productDiscountPrice(cartItem.getProduct().getDiscountPrice().setScale(2, RoundingMode.HALF_UP))
                .quantity(cartItem.getQuantity())
                .build();
    }

    public Set<CartItemFullResponseDto> toFullDtos(Set<CartItem> cartItems) {
        return cartItems.stream()
                .map(this::toFullDto)
                .collect(Collectors.toSet());
    }

    public OrderItem cartItemToOrderItem(CartItem cartItem) {
        Product product = cartItem.getProduct();

        BigDecimal quantity = BigDecimal.valueOf(cartItem.getQuantity());
        BigDecimal price = product.getPrice();
        BigDecimal discount = product.getDiscountPrice() != null
                ? product.getDiscountPrice()
                : BigDecimal.ZERO;

        BigDecimal totalPrice = priceCalculator.calculateDiscountedPrice(price, discount);

        return OrderItem.builder()
                .product(product)
                .quantity(cartItem.getQuantity())
                .priceAtPurchase(totalPrice)
                .build();
    }

    public CartItemSimpleResponseDto toSimpleDto(CartItem cartItem) {
        return CartItemSimpleResponseDto.builder()
                .productName(cartItem.getProduct().getName())
                .quantity(cartItem.getQuantity())
                .build();
    }

    public CartItemSimpleResponseDto toSimpleDtoFromDto(CartItemResponseDto cartItemResponseDto) {
        return CartItemSimpleResponseDto.builder()
                .productName(cartItemResponseDto.getProduct().getName())
                .quantity(cartItemResponseDto.getQuantity())
                .build();
    }
}
