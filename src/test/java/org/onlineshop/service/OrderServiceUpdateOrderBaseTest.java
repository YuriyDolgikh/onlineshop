package org.onlineshop.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.onlineshop.dto.order.OrderRequestDto;
import org.onlineshop.dto.order.OrderResponseDto;
import org.onlineshop.dto.order.OrderStatusResponseDto;
import org.onlineshop.entity.Order;
import org.onlineshop.exception.BadRequestException;
import org.onlineshop.exception.NotFoundException;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceUpdateOrderBaseTest extends OrderServiceBaseTest {

    @Test
    void updateOrderStatus_whenAccessDenied_shouldThrowAccessDenied() {
        Integer orderId = 10;
        doReturn(false).when(orderService).isAccessToOrderAllowed(orderId);

        assertThrows(
                AccessDeniedException.class,
                () -> orderService.updateOrderStatus(orderId, "PAID")
        );
    }

    @Test
    void updateOrderStatus_whenNewStatusBlank_shouldThrowBadRequest() {
        Integer orderId = 10;
        doReturn(true).when(orderService).isAccessToOrderAllowed(orderId);

        assertThrows(
                BadRequestException.class,
                () -> orderService.updateOrderStatus(orderId, " "),
                "Expected BadRequestException for blank status"
        );
    }

    @Test
    void updateOrderStatus_whenOrderNotFound_shouldThrowNotFound() {
        Integer orderId = 10;
        doReturn(true).when(orderService).isAccessToOrderAllowed(orderId);
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> orderService.updateOrderStatus(orderId, "PAID")
        );
    }

    @Test
    void updateOrderStatus_whenOk_shouldUpdateStatusAndReturnDto() {
        Integer orderId = 10;
        Order order = Order.builder().orderId(orderId).status(Order.Status.PENDING_PAYMENT).build();
        OrderResponseDto dto = new OrderResponseDto();

        doReturn(true).when(orderService).isAccessToOrderAllowed(orderId);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderConverter.toDto(order)).thenReturn(dto);

        OrderResponseDto result = orderService.updateOrderStatus(orderId, "PAID");

        assertSame(dto, result);
        assertEquals(Order.Status.PAID, order.getStatus());
        verify(orderRepository).save(order);
    }

    @Test
    void cancelOrder_whenAccessDenied_shouldThrowAccessDenied() {
        Integer orderId = 10;
        doReturn(false).when(orderService).isAccessToOrderAllowed(orderId);

        assertThrows(
                AccessDeniedException.class,
                () -> orderService.cancelOrder(orderId)
        );
    }

    @Test
    void cancelOrder_whenOrderNotFound_shouldThrowNotFound() {
        Integer orderId = 10;
        doReturn(true).when(orderService).isAccessToOrderAllowed(orderId);
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> orderService.cancelOrder(orderId)
        );
    }

    @Test
    void cancelOrder_whenOk_shouldSetStatusCancelled() {
        Integer orderId = 10;
        Order order = Order.builder().orderId(orderId).status(Order.Status.PENDING_PAYMENT).build();

        doReturn(true).when(orderService).isAccessToOrderAllowed(orderId);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        orderService.cancelOrder(orderId);

        assertEquals(Order.Status.CANCELLED, order.getStatus());
        verify(orderRepository).save(order);
    }

    @Test
    void updateOrderDelivery_whenAccessDenied_shouldThrowAccessDenied() {
        Integer orderId = 10;
        doReturn(false).when(orderService).isAccessToOrderAllowed(orderId);

        assertThrows(
                AccessDeniedException.class,
                () -> orderService.updateOrderDelivery(orderId, new OrderRequestDto())
        );
    }

    @Test
    void updateOrderDelivery_whenDtoNull_shouldThrowIllegalArgument() {
        Integer orderId = 10;
        doReturn(true).when(orderService).isAccessToOrderAllowed(orderId);

        assertThrows(
                IllegalArgumentException.class,
                () -> orderService.updateOrderDelivery(orderId, null)
        );
    }

    @Test
    void updateOrderDelivery_whenAddressBlank_shouldThrowIllegalArgument() {
        Integer orderId = 10;
        doReturn(true).when(orderService).isAccessToOrderAllowed(orderId);

        OrderRequestDto dto = OrderRequestDto.builder()
                .deliveryAddress(" ")
                .contactPhone("123")
                .build();

        assertThrows(
                IllegalArgumentException.class,
                () -> orderService.updateOrderDelivery(orderId, dto)
        );
    }

    @Test
    void updateOrderDelivery_whenPhoneBlank_shouldThrowIllegalArgument() {
        Integer orderId = 10;
        doReturn(true).when(orderService).isAccessToOrderAllowed(orderId);

        OrderRequestDto dto = OrderRequestDto.builder()
                .deliveryAddress("Some address")
                .contactPhone(" ")
                .build();

        assertThrows(
                IllegalArgumentException.class,
                () -> orderService.updateOrderDelivery(orderId, dto)
        );
    }

    @Test
    void updateOrderDelivery_whenOrderNotFound_shouldThrowRuntimeException() {
        Integer orderId = 10;
        doReturn(true).when(orderService).isAccessToOrderAllowed(orderId);

        OrderRequestDto dto = OrderRequestDto.builder()
                .deliveryAddress("Some address")
                .contactPhone("123")
                .deliveryMethod("COURIER")
                .build();

        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThrows(
                RuntimeException.class,
                () -> orderService.updateOrderDelivery(orderId, dto),
                "Expected RuntimeException 'Order not found'"
        );
    }

    @Test
    void updateOrderDelivery_whenDeliveryMethodInvalid_shouldThrowRuntimeException() {
        Integer orderId = 10;
        doReturn(true).when(orderService).isAccessToOrderAllowed(orderId);

        OrderRequestDto dto = OrderRequestDto.builder()
                .deliveryAddress("Some address")
                .contactPhone("123")
                .deliveryMethod("INVALID")
                .build();

        Order order = Order.builder().orderId(orderId).status(Order.Status.PENDING_PAYMENT).build();
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> orderService.updateOrderDelivery(orderId, dto)
        );
        assertTrue(ex.getMessage().contains("Invalid delivery method"));
    }

    @Test
    void updateOrderDelivery_whenOk_shouldUpdateFieldsAndReturnDto() {
        Integer orderId = 10;
        doReturn(true).when(orderService).isAccessToOrderAllowed(orderId);

        OrderRequestDto dto = OrderRequestDto.builder()
                .deliveryAddress("New address")
                .contactPhone("987654")
                .deliveryMethod("COURIER")
                .build();

        Order order = Order.builder()
                .orderId(orderId)
                .status(Order.Status.PENDING_PAYMENT)
                .build();
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        OrderResponseDto responseDto = new OrderResponseDto();
        when(orderConverter.toDto(order)).thenReturn(responseDto);

        OrderResponseDto result = orderService.updateOrderDelivery(orderId, dto);

        assertSame(responseDto, result);
        assertEquals("New address", order.getDeliveryAddress());
        assertEquals("987654", order.getContactPhone());
        assertEquals(Order.DeliveryMethod.COURIER, order.getDeliveryMethod());
        verify(orderRepository).save(order);
    }

    @Test
    void getOrderStatusDto_whenAccessDenied_shouldThrowAccessDenied() {
        Integer orderId = 10;
        doReturn(false).when(orderService).isAccessToOrderAllowed(orderId);

        assertThrows(
                AccessDeniedException.class,
                () -> orderService.getOrderStatusDto(orderId)
        );
    }

    @Test
    void getOrderStatusDto_whenOrderNotFound_shouldThrowNotFound() {
        Integer orderId = 10;
        doReturn(true).when(orderService).isAccessToOrderAllowed(orderId);
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> orderService.getOrderStatusDto(orderId)
        );
    }

    @Test
    void getOrderStatusDto_whenOk_shouldReturnStatusDto() {
        Integer orderId = 10;
        doReturn(true).when(orderService).isAccessToOrderAllowed(orderId);

        LocalDateTime updated = LocalDateTime.now();
        Order order = Order.builder()
                .orderId(orderId)
                .status(Order.Status.PAID)
                .updatedAt(updated)
                .build();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        OrderStatusResponseDto dto = orderService.getOrderStatusDto(orderId);

        assertEquals("PAID", dto.getStatus());
        assertEquals(updated, dto.getUpdatedAt());
    }

}