package org.onlineshop.service.converter;

import org.onlineshop.dto.orderItem.OrderItemResponseDto;
import org.onlineshop.entity.OrderItem;
import org.springframework.stereotype.Service;

@Service
public class OrderItemConverter {
    public  OrderItemResponseDto fromEntity(OrderItem orderItem) {
        if (orderItem == null) {
            return null;
        }

        return OrderItemResponseDto.builder()
                .orderItemId(orderItem.getOrderItemId())
                .productId(orderItem.getProduct().getId())
                .quantity(orderItem.getQuantity())
                .priceAtPurchase(orderItem.getPriceAtPurchase())
                .build();
    }
}
