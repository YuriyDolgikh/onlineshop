package org.onlineshop.service.converter;

import lombok.Generated;
import org.onlineshop.dto.orderItem.OrderItemResponseDto;
import org.onlineshop.entity.OrderItem;
import org.springframework.stereotype.Service;

@Generated
@Service
public class OrderItemConverter {
    public  OrderItemResponseDto toDto(OrderItem orderItem) {
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
