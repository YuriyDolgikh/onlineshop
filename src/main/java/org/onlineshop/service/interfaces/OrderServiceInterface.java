package org.onlineshop.service.interfaces;

import org.onlineshop.dto.order.OrderRequestDto;
import org.onlineshop.dto.order.OrderResponseDto;
import org.onlineshop.dto.order.OrderStatusResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderServiceInterface {
    OrderResponseDto getOrderById(Integer orderId);

    Page<OrderResponseDto> getOrdersByUser(Integer userId, Pageable pageable);

    OrderResponseDto updateOrderStatus(Integer orderId, String newStatus);

    void cancelOrder(Integer orderId);

    OrderResponseDto confirmPayment(Integer orderId, String paymentMethod);

    OrderResponseDto updateOrderDelivery(Integer orderId, OrderRequestDto orderRequestDto);

    OrderStatusResponseDto getOrderStatusDto(Integer orderId);

    void transferCartToOrder();
}
