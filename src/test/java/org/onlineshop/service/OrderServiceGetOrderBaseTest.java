package org.onlineshop.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.onlineshop.dto.order.OrderResponseDto;
import org.onlineshop.entity.Order;
import org.onlineshop.entity.User;
import org.onlineshop.exception.NotFoundException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.springframework.security.access.AccessDeniedException;

@ExtendWith(MockitoExtension.class)
class OrderServiceGetOrderBaseTest extends OrderServiceBaseTest {

    @Test
    void getOrderById_whenAccessDenied_shouldThrowAccessDeniedException() {
        Integer orderId = 10;
        doReturn(false).when(orderService).isAccessToOrderAllowed(orderId);

        assertThrows(
                AccessDeniedException.class,
                () -> orderService.getOrderById(orderId),
                "Expected AccessDeniedException when access not allowed"
        );
    }

    @Test
    void getOrderById_whenOrderNotFound_shouldThrowNotFoundException() {
        Integer orderId = 10;
        doReturn(true).when(orderService).isAccessToOrderAllowed(orderId);
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> orderService.getOrderById(orderId),
                "Expected NotFoundException when order not found"
        );
    }

    @Test
    void getOrderById_whenOk_shouldReturnDto() {
        Integer orderId = 10;
        Order order = Order.builder().orderId(orderId).build();
        OrderResponseDto dto = new OrderResponseDto();

        doReturn(true).when(orderService).isAccessToOrderAllowed(orderId);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderConverter.toDto(order)).thenReturn(dto);

        OrderResponseDto result = orderService.getOrderById(orderId);
        assertSame(dto, result);
    }

    @Test
    void getOrdersByUser_whenUserIdNull_shouldThrowIllegalArgumentException() {
        assertThrows(
                IllegalArgumentException.class,
                () -> orderService.getOrdersByUser(null),
                "Expected IllegalArgumentException when userId is null"
        );
    }

    @Test
    void getOrdersByUser_whenUserNotFound_shouldThrowNotFoundException() {
        Integer userId = 10;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> orderService.getOrdersByUser(userId),
                "Expected NotFoundException when user not found"
        );
    }

    @Test
    void getOrdersByUser_whenOk_shouldReturnDtos() {
        Integer userId = 3;
        User user = userRegular;
        user.setUserId(userId);

        Order order1 = Order.builder().orderId(1).user(user).build();
        Order order2 = Order.builder().orderId(2).user(user).build();
        List<Order> orders = List.of(order1, order2);

        OrderResponseDto dto1 = new OrderResponseDto();
        OrderResponseDto dto2 = new OrderResponseDto();
        List<OrderResponseDto> dtos = List.of(dto1, dto2);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(orderRepository.findByUser(user)).thenReturn(orders);
        when(orderConverter.toDtos(orders)).thenReturn(dtos);

        List<OrderResponseDto> result = orderService.getOrdersByUser(userId);
        assertEquals(2, result.size());
        assertSame(dtos, result);
    }

}