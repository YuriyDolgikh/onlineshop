package org.onlineshop.service.converter;

import org.onlineshop.dto.cartItem.CartItemResponseDto;
import org.onlineshop.entity.OrderItem;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartItemConverter {
    public CartItemResponseDto toDto(OrderItem orderItem) {
        if (orderItem == null) {
            return null;
        }
       return CartItemResponseDto.builder()
               .product(orderItem.getProduct())
               .quantity(orderItem.getQuantity())
               .build();
    }

    public List<CartItemResponseDto> toDtos(List<OrderItem> orderItems){
        return orderItems.stream()
                .map(this::toDto)
                .toList();
    }

}
