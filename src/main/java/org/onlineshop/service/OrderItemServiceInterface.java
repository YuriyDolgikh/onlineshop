package org.onlineshop.service;

import org.onlineshop.dto.orderItem.OrderItemRequestDto;
import org.onlineshop.dto.orderItem.OrderItemResponseDto;
import org.onlineshop.dto.orderItem.OrderItemUpdateDto;

public interface OrderItemServiceInterface {
    OrderItemResponseDto addItemToOrder(OrderItemRequestDto orderItemRequestDto);
    void deleteItemFromOrder(Integer orderItemId);
    OrderItemResponseDto updateItemQuantityInOrder(OrderItemUpdateDto orderItemUpdateDto);
}
