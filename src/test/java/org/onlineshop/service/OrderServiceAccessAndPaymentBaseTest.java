package org.onlineshop.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.onlineshop.dto.order.OrderResponseDto;
import org.onlineshop.entity.Order;
import org.onlineshop.entity.User;
import org.onlineshop.exception.BadRequestException;
import org.onlineshop.exception.MailSendingException;
import org.onlineshop.exception.NotFoundException;
import org.onlineshop.service.util.PdfOrderGenerator;
import org.springframework.security.access.AccessDeniedException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceAccessAndPaymentBaseTest extends OrderServiceBaseTest {

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

        assertThrows(
                NotFoundException.class,
                () -> orderService.isAccessToOrderAllowed(orderId)
        );
    }

    @Test
    void isAccessToOrderAllowed_whenAdmin_shouldReturnTrue() {
        Integer orderId = 10;
        Order order = Order.builder().orderId(orderId).user(userRegular).build();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(userService.getCurrentUser()).thenReturn(userAdmin);

        assertTrue(orderService.isAccessToOrderAllowed(orderId));
    }

    @Test
    void isAccessToOrderAllowed_whenManager_shouldReturnTrue() {
        Integer orderId = 10;
        Order order = Order.builder().orderId(orderId).user(userRegular).build();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
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
        Order order = Order.builder().orderId(orderId).user(otherUser).status(Order.Status.PENDING_PAYMENT).build();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(userService.getCurrentUser()).thenReturn(userRegular);

        assertThrows(
                AccessDeniedException.class,
                () -> orderService.confirmPayment(orderId, "CARD")
        );
    }

    @Test
    void confirmPayment_whenMailSendFails_shouldThrowMailSendingException() {
        Integer orderId = 10;
        Order order = Order.builder()
                .orderId(orderId)
                .user(userRegular)
                .status(Order.Status.PENDING_PAYMENT)
                .build();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(userService.getCurrentUser()).thenReturn(userRegular);

        byte[] pdfBytes = "dummy".getBytes();

        try (MockedStatic<PdfOrderGenerator> mockedStatic = mockStatic(PdfOrderGenerator.class)) {
            mockedStatic.when(() -> PdfOrderGenerator.generatePdfOrder(order))
                    .thenReturn(pdfBytes);

            doThrow(new RuntimeException("SMTP error"))
                    .when(mailUtil).sendOrderPaidEmail(userRegular, order, pdfBytes);

            assertThrows(
                    MailSendingException.class,
                    () -> orderService.confirmPayment(orderId, "CARD"),
                    "Expected MailSendingException on email failure"
            );
        }
    }

    @Test
    void confirmPayment_whenOk_shouldSetStatusPaidAndSendEmail() {
        Integer orderId = 10;
        Order order = Order.builder()
                .orderId(orderId)
                .user(userRegular)
                .status(Order.Status.PENDING_PAYMENT)
                .build();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(userService.getCurrentUser()).thenReturn(userRegular);

        OrderResponseDto dto = new OrderResponseDto();
        when(orderConverter.toDto(order)).thenReturn(dto);

        byte[] pdfBytes = "dummy".getBytes();

        try (MockedStatic<PdfOrderGenerator> mockedStatic = mockStatic(PdfOrderGenerator.class)) {
            mockedStatic.when(() -> PdfOrderGenerator.generatePdfOrder(order))
                    .thenReturn(pdfBytes);

            OrderResponseDto result = orderService.confirmPayment(orderId, "CARD");

            assertSame(dto, result);
            assertEquals(Order.Status.PAID, order.getStatus());
            verify(orderRepository).save(order);
            verify(mailUtil).sendOrderPaidEmail(userRegular, order, pdfBytes);
        }
    }
}
