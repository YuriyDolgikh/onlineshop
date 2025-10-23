package org.onlineshop.service;

import org.onlineshop.dto.orderItem.OrderItemResponseDto;

public interface OrderItemServiceInterface {
    OrderItemResponseDto addItemToOrder(Integer productId, Integer quantity);
}
