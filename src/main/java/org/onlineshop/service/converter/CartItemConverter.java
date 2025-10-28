package org.onlineshop.service.converter;

import org.onlineshop.dto.cartItem.CartItemResponseDto;
import org.onlineshop.entity.OrderItem;
import org.springframework.stereotype.Service;

@Service
public class CartItemConverter {
    public CartItemResponseDto fromEntity(OrderItem orderItem) {
        if (orderItem == null) {
            return null;
        }
       return CartItemResponseDto.builder()
               .product(orderItem.getProduct())
               .quantity(orderItem.getQuantity())
               .build();
    }

}
