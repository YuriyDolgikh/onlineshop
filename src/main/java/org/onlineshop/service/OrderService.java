package org.onlineshop.service;

import org.onlineshop.dto.order.OrderRequestDto;
import org.onlineshop.dto.order.OrderResponseDto;

import java.util.List;

public class OrderService implements OrderServiceInterface{
    @Override
    public OrderResponseDto saveOrder(OrderRequestDto orderRequestDto) {
        return null;
    }

    @Override
    public OrderResponseDto getOrderById(Integer orderId) {
        return null;
    }

    @Override
    public List<OrderResponseDto> getOrdersByUser(Integer userId) {
        return List.of();
    }

    @Override
    public OrderResponseDto updateOrderStatus(Integer orderId, String newStatus) {
        return null;
    }

    @Override
    public void cancelOrder(Integer orderId) {

    }

    @Override
    public OrderResponseDto confirmPayment(Integer orderId, String paymentMethod) {
        return null;
    }
}
