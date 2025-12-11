package org.onlineshop.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.onlineshop.dto.order.OrderResponseDto;
import org.onlineshop.entity.Order;
import org.onlineshop.entity.User;
import org.onlineshop.exception.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
                () -> orderService.getOrdersByUser(null, Pageable.unpaged()),
                "Expected IllegalArgumentException when userId is null"
        );
    }

    @Test
    void getOrdersByUser_whenUserNotFound_shouldThrowNotFoundException() {
        Integer userId = 99;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> orderService.getOrdersByUser(userId, Pageable.unpaged()),
                "Expected NotFoundException when user not found"
        );
    }

    @Test
    void getOrdersByUser_whenRegularUserAccessingOwnOrders_shouldReturnOrders() {
        Integer userId = 5;
        User user = User.builder()
                .userId(userId)
                .role(User.Role.USER)
                .build();

        Order order1 = Order.builder().orderId(10).user(user).build();
        Order order2 = Order.builder().orderId(11).user(user).build();
        List<Order> orders = List.of(order1, order2);
        Page<Order> ordersPage = new PageImpl<>(orders);

        OrderResponseDto dto1 = new OrderResponseDto();
        OrderResponseDto dto2 = new OrderResponseDto();
        List<OrderResponseDto> dtos = List.of(dto1, dto2);
        Page<OrderResponseDto> dtosPage = new PageImpl<>(dtos);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userService.getCurrentUser()).thenReturn(user);
        when(orderRepository.findByUser(user, Pageable.unpaged())).thenReturn(ordersPage);
        when(orderConverter.toDtos(orders)).thenReturn(dtos);

        Page<OrderResponseDto> result = orderService.getOrdersByUser(userId, Pageable.unpaged());

        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals(dtosPage.getContent(), result.getContent());
        verify(userService).getCurrentUser();
        verify(orderRepository).findByUser(user, Pageable.unpaged());
    }

    @Test
    void getOrdersByUser_whenRegularUserAccessingForeignOrders_shouldThrowAccessDenied() {
        Integer requestedUserId = 99;
        Integer currentUserId = 5;

        User requestedUser = User.builder()
                .userId(requestedUserId)
                .role(User.Role.USER)
                .build();

        User currentUser = User.builder()
                .userId(currentUserId)
                .role(User.Role.USER)
                .build();

        when(userRepository.findById(requestedUserId)).thenReturn(Optional.of(requestedUser));
        when(userService.getCurrentUser()).thenReturn(currentUser);

        assertThrows(
                AccessDeniedException.class,
                () -> orderService.getOrdersByUser(requestedUserId, Pageable.unpaged()),
                "Expected AccessDeniedException when USER tries to access foreign user's orders"
        );

        verify(userService).getCurrentUser();
        verify(orderRepository, never()).findByUser(any(), any());
    }

    @Test
    void getOrdersByUser_whenAdminAccessingOtherUserOrders_shouldReturnOrders() {
        Integer requestedUserId = 99;
        Integer adminUserId = 1;

        User requestedUser = User.builder()
                .userId(requestedUserId)
                .role(User.Role.USER)
                .build();

        User adminUser = User.builder()
                .userId(adminUserId)
                .role(User.Role.ADMIN)
                .build();

        Order order1 = Order.builder().orderId(1).user(requestedUser).build();
        List<Order> orders = List.of(order1);
        Page<Order> ordersPage = new PageImpl<>(orders);

        OrderResponseDto dto1 = new OrderResponseDto();
        List<OrderResponseDto> dtos = List.of(dto1);
        Page<OrderResponseDto> dtosPage = new PageImpl<>(dtos);

        when(userRepository.findById(requestedUserId)).thenReturn(Optional.of(requestedUser));
        when(userService.getCurrentUser()).thenReturn(adminUser);
        when(orderRepository.findByUser(requestedUser, Pageable.unpaged())).thenReturn(ordersPage);
        when(orderConverter.toDtos(orders)).thenReturn(dtos);

        Page<OrderResponseDto> result = orderService.getOrdersByUser(requestedUserId, Pageable.unpaged());

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(dtosPage.getContent(), result.getContent());
        verify(orderRepository).findByUser(requestedUser, Pageable.unpaged());
    }

    @Test
    void getOrdersByUser_whenManagerAccessingOtherUserOrders_shouldReturnOrders() {
        Integer requestedUserId = 50;
        Integer managerUserId = 2;

        User requestedUser = User.builder()
                .userId(requestedUserId)
                .role(User.Role.USER)
                .build();

        User managerUser = User.builder()
                .userId(managerUserId)
                .role(User.Role.MANAGER)
                .build();

        Order order1 = Order.builder().orderId(20).user(requestedUser).build();
        List<Order> orders = List.of(order1);
        Page<Order> ordersPage = new PageImpl<>(orders);

        OrderResponseDto dto1 = new OrderResponseDto();
        List<OrderResponseDto> dtos = List.of(dto1);
        Page<OrderResponseDto> dtosPage = new PageImpl<>(dtos);

        when(userRepository.findById(requestedUserId)).thenReturn(Optional.of(requestedUser));
        when(userService.getCurrentUser()).thenReturn(managerUser);
        when(orderRepository.findByUser(requestedUser, Pageable.unpaged())).thenReturn(ordersPage);
        when(orderConverter.toDtos(orders)).thenReturn(dtos);

        Page<OrderResponseDto> result = orderService.getOrdersByUser(requestedUserId, Pageable.unpaged());

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(dtosPage.getContent(), result.getContent());
        verify(orderRepository).findByUser(requestedUser, Pageable.unpaged());
    }

    @Test
    void getOrdersByUser_whenUserHasNoOrders_shouldReturnEmptyPage() {
        Integer userId = 7;
        User user = User.builder()
                .userId(userId)
                .role(User.Role.USER)
                .build();

        List<Order> emptyOrders = List.of();
        Page<Order> emptyOrdersPage = new PageImpl<>(emptyOrders);
        List<OrderResponseDto> emptyDtos = List.of();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userService.getCurrentUser()).thenReturn(user);
        when(orderRepository.findByUser(user, Pageable.unpaged())).thenReturn(emptyOrdersPage);
        when(orderConverter.toDtos(emptyOrders)).thenReturn(emptyDtos);

        Page<OrderResponseDto> result = orderService.getOrdersByUser(userId, Pageable.unpaged());

        assertNotNull(result);
        assertTrue(result.isEmpty());
        assertEquals(0, result.getTotalElements());
        verify(orderRepository).findByUser(user, Pageable.unpaged());
    }
}