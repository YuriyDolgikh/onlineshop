package org.onlineshop.service.converter;

import lombok.RequiredArgsConstructor;
import org.onlineshop.dto.order.OrderResponseDto;
import org.onlineshop.dto.orderItem.OrderItemResponseDto;
import org.onlineshop.entity.Order;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderConverter {
    public OrderResponseDto fromEntity(Order order) {
        if (order == null) {
            return null;
        }

        return OrderResponseDto.builder()
                .orderId(order.getOrderId())
                .userId(order.getUser().getUserId())
                .deliveryAddress(order.getDeliveryAddress())
                .contactPhone(order.getContactPhone())
                .deliveryMethod(order.getDeliveryMethod().name())
                .status(order.getStatus().name())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .items(order.getOrderItems().stream()
                        .map(i -> OrderItemResponseDto.builder()
                                .orderItemId(i.getOrderItemId())
                                .productId(i.getProduct().getId())
                                .quantity(i.getQuantity())
                                .priceAtPurchase(i.getPriceAtPurchase())
                                .build())
                        .toList())
                .build();

    }

}
