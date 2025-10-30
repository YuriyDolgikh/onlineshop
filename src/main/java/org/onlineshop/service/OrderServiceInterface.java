package org.onlineshop.service;

import org.onlineshop.dto.order.OrderRequestDto;
import org.onlineshop.dto.order.OrderResponseDto;
import org.onlineshop.dto.order.OrderStatusResponseDto;

import java.util.List;

public interface OrderServiceInterface {
    OrderResponseDto saveOrder(OrderRequestDto orderRequestDto);
    OrderResponseDto getOrderById(Integer orderId);
    List<OrderResponseDto> getOrdersByUser(Integer userId);
    OrderResponseDto updateOrderStatus(Integer orderId, String newStatus);
    void cancelOrder(Integer orderId);
    OrderResponseDto confirmPayment(Integer orderId, String paymentMethod);
    OrderResponseDto updateOrderDelivery(Integer orderId, OrderRequestDto orderRequestDto);
    OrderStatusResponseDto getOrderStatusDto(Integer orderId);
}
