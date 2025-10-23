package org.onlineshop.service;

import org.onlineshop.dto.order.OrderRequestDto;
import org.onlineshop.dto.order.OrderResponseDto;

public interface OrderServiceInterface {
    OrderResponseDto saveOrder(OrderRequestDto orderRequestDto);
}
