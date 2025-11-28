package org.onlineshop.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.onlineshop.dto.order.OrderResponseDto;
import org.onlineshop.entity.Order;
import org.onlineshop.entity.OrderItem;
import org.onlineshop.entity.Product;
import org.onlineshop.entity.User;
import org.onlineshop.exception.BadRequestException;
import org.onlineshop.exception.NotFoundException;
import org.onlineshop.service.util.PdfOrderGenerator;
import org.springframework.security.access.AccessDeniedException;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceAccessAndPaymentBaseTest extends OrderServiceBaseTest {

    private OrderItem createMockOrderItem() {
        Product product = mock(Product.class);
        lenient().when(product.getPrice()).thenReturn(new BigDecimal("100.00"));
        lenient().when(product.getDiscountPrice()).thenReturn(new BigDecimal("10.00"));

        OrderItem orderItem = mock(OrderItem.class);
        lenient().when(orderItem.getProduct()).thenReturn(product);
        lenient().when(orderItem.getPriceAtPurchase()).thenReturn(new BigDecimal("90.00"));
        lenient().when(orderItem.getQuantity()).thenReturn(1);

        return orderItem;
    }

    private Order createOrderWithItems(User user, Order.Status status) {
        Order order = Order.builder()
                .orderId(10)
                .user(user)
                .status(status)
                .build();

        OrderItem orderItem = createMockOrderItem();
        lenient().when(orderItem.getOrder()).thenReturn(order);

        order.setOrderItems(Arrays.asList(orderItem));
        return order;
    }

    @Test
    void isAccessToOrderAllowed_whenOrderIdNull_shouldThrowBadRequest() {
        assertThrows(
                BadRequestException.class,
                () -> orderService.isAccessToOrderAllowed(null)
        );
    }

    @Test
    void isAccessToOrderAllowed_whenOrderNotFound_shouldThrowNotFound() {
        Integer orderId = 10;
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());
        when(userService.getCurrentUser()).thenReturn(userRegular);

        assertThrows(
                NotFoundException.class,
                () -> orderService.isAccessToOrderAllowed(orderId)
        );
    }

    @Test
    void isAccessToOrderAllowed_whenAdmin_shouldReturnTrue() {
        Integer orderId = 10;
        Order order = Order.builder().orderId(orderId).user(userRegular).build();

        lenient().when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(userService.getCurrentUser()).thenReturn(userAdmin);

        assertTrue(orderService.isAccessToOrderAllowed(orderId));
    }

    @Test
    void isAccessToOrderAllowed_whenManager_shouldReturnTrue() {
        Integer orderId = 10;
        Order order = Order.builder().orderId(orderId).user(userRegular).build();

        lenient().when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(userService.getCurrentUser()).thenReturn(userManager);

        assertTrue(orderService.isAccessToOrderAllowed(orderId));
    }

    @Test
    void isAccessToOrderAllowed_whenUserOwnOrder_shouldReturnTrue() {
        Integer orderId = 10;
        Order order = Order.builder().orderId(orderId).user(userRegular).build();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(userService.getCurrentUser()).thenReturn(userRegular);

        assertTrue(orderService.isAccessToOrderAllowed(orderId));
    }

    @Test
    void isAccessToOrderAllowed_whenUserForeignOrder_shouldReturnFalse() {
        Integer orderId = 10;
        User otherUser = User.builder().userId(99).role(User.Role.USER).build();
        Order order = Order.builder().orderId(orderId).user(otherUser).build();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(userService.getCurrentUser()).thenReturn(userRegular);

        assertFalse(orderService.isAccessToOrderAllowed(orderId));
    }

    /* ========== confirmPayment() ========== */

    @Test
    void confirmPayment_whenOrderNotFound_shouldThrowNotFound() {
        Integer orderId = 10;
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> orderService.confirmPayment(orderId, "CARD")
        );
    }

    @Test
    void confirmPayment_whenCurrentUserDifferent_shouldThrowAccessDenied() {
        Integer orderId = 10;
        User otherUser = User.builder().userId(99).role(User.Role.USER).build();
        Order order = createOrderWithItems(otherUser, Order.Status.PENDING_PAYMENT);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(userService.getCurrentUser()).thenReturn(userRegular);

        assertThrows(
                AccessDeniedException.class,
                () -> orderService.confirmPayment(orderId, "CARD")
        );
    }

    @Test
    void confirmPayment_whenPaymentMethodBlank_shouldThrowBadRequest() {
        Integer orderId = 10;
        Order order = createOrderWithItems(userRegular, Order.Status.PENDING_PAYMENT);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(userService.getCurrentUser()).thenReturn(userRegular);

        assertThrows(
                BadRequestException.class,
                () -> orderService.confirmPayment(orderId, " ")
        );
    }

    @Test
    void confirmPayment_whenOrderStatusNotPendingPayment_shouldThrowBadRequest() {
        Integer orderId = 10;
        Order order = createOrderWithItems(userRegular, Order.Status.PAID);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(userService.getCurrentUser()).thenReturn(userRegular);

        assertThrows(
                BadRequestException.class,
                () -> orderService.confirmPayment(orderId, "CARD")
        );
    }

    @Test
    void confirmPayment_whenOk_shouldSetStatusPaidAndSendEmail() {
        Integer orderId = 10;
        Order order = createOrderWithItems(userRegular, Order.Status.PENDING_PAYMENT);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(userService.getCurrentUser()).thenReturn(userRegular);

        doNothing().when(orderService).recalculateOrderPrice(order);

        OrderResponseDto dto = new OrderResponseDto();
        when(orderConverter.toDto(order)).thenReturn(dto);

        byte[] pdfBytes = "dummy".getBytes();

        try (MockedStatic<PdfOrderGenerator> mockedStatic = mockStatic(PdfOrderGenerator.class)) {
            mockedStatic.when(() -> PdfOrderGenerator.generatePdfOrder(order))
                    .thenReturn(pdfBytes);

            OrderResponseDto result = orderService.confirmPayment(orderId, "CARD");

            assertSame(dto, result);
            assertEquals(Order.Status.PAID, order.getStatus());

            verify(orderRepository, times(1)).save(order);
            verify(cartService).clearCart();
        }
    }
}