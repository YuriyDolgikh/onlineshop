package org.onlineshop.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.onlineshop.dto.order.OrderResponseDto;
import org.onlineshop.exception.BadRequestException;
import org.onlineshop.exception.MailSendingException;
import org.onlineshop.exception.NotFoundException;
import org.onlineshop.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
@DisplayName("OrderController unit tests")
class OrderControllerTest {

    private static final Integer ORDER_ID = 123;
    private static final Integer USER_ID = 456;
    private static final String PAY_METHOD = "CARD";
    @Mock
    private OrderService orderService;
    @InjectMocks
    private OrderController orderController;

    @Nested
    @DisplayName("getOrderById() endpoint tests")
    class GetOrderByIdTests {

        @Test
        @DisplayName("Should return 200 OK and order body when order exists and access is allowed")
        void getOrderById_whenOrderExists_shouldReturnOk() {
            OrderResponseDto serviceResponse = new OrderResponseDto();
            when(orderService.getOrderById(ORDER_ID)).thenReturn(serviceResponse);

            ResponseEntity<OrderResponseDto> response = orderController.getOrderById(ORDER_ID);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertSame(serviceResponse, response.getBody());
            verify(orderService).getOrderById(ORDER_ID);
        }

        @Test
        @DisplayName("Should propagate NotFoundException when order does not exist")
        void getOrderById_whenOrderNotFound_shouldPropagateNotFound() {
            when(orderService.getOrderById(ORDER_ID))
                    .thenThrow(new NotFoundException("Order not found"));

            NotFoundException ex = assertThrows(
                    NotFoundException.class,
                    () -> orderController.getOrderById(ORDER_ID)
            );

            assertEquals("Order not found", ex.getMessage());
            verify(orderService).getOrderById(ORDER_ID);
        }

        @Test
        @DisplayName("Should propagate AccessDeniedException when user has no access to the order")
        void getOrderById_whenAccessDenied_shouldPropagateAccessDenied() {
            when(orderService.getOrderById(ORDER_ID))
                    .thenThrow(new AccessDeniedException("Access denied"));

            AccessDeniedException ex = assertThrows(
                    AccessDeniedException.class,
                    () -> orderController.getOrderById(ORDER_ID)
            );

            assertEquals("Access denied", ex.getMessage());
            verify(orderService).getOrderById(ORDER_ID);
        }
    }

    @Nested
    @DisplayName("getOrdersByUser() endpoint tests")
    class GetOrdersByUserTests {

        @Test
        @DisplayName("Should return 200 OK and list of orders when user exists and access is allowed")
        void getOrdersByUser_whenOk_shouldReturnOrderList() {
            OrderResponseDto dto1 = new OrderResponseDto();
            OrderResponseDto dto2 = new OrderResponseDto();
            List<OrderResponseDto> serviceResponse = List.of(dto1, dto2);

            when(orderService.getOrdersByUser(USER_ID)).thenReturn(serviceResponse);

            ResponseEntity<List<OrderResponseDto>> response = orderController.getOrdersByUser(USER_ID);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertSame(serviceResponse, response.getBody());
            assertNotNull(response.getBody());
            assertEquals(2, response.getBody().size());
            verify(orderService).getOrdersByUser(USER_ID);
        }

        @Test
        @DisplayName("Should propagate NotFoundException when user does not exist")
        void getOrdersByUser_whenUserNotFound_shouldPropagateNotFound() {
            when(orderService.getOrdersByUser(USER_ID))
                    .thenThrow(new NotFoundException("User not found"));

            NotFoundException ex = assertThrows(
                    NotFoundException.class,
                    () -> orderController.getOrdersByUser(USER_ID)
            );

            assertEquals("User not found", ex.getMessage());
            verify(orderService).getOrdersByUser(USER_ID);
        }

        @Test
        @DisplayName("Should propagate AccessDeniedException when regular user tries to access foreign orders")
        void getOrdersByUser_whenAccessDenied_shouldPropagateAccessDenied() {
            when(orderService.getOrdersByUser(USER_ID))
                    .thenThrow(new AccessDeniedException("Access denied"));

            AccessDeniedException ex = assertThrows(
                    AccessDeniedException.class,
                    () -> orderController.getOrdersByUser(USER_ID)
            );

            assertEquals("Access denied", ex.getMessage());
            verify(orderService).getOrdersByUser(USER_ID);
        }
    }

    @Nested
    @DisplayName("cancelOrder() endpoint tests")
    class CancelOrderTests {

        @Test
        @DisplayName("Should return 200 OK when order is successfully cancelled")
        void cancelOrder_whenOk_shouldReturnOk() {
            doNothing().when(orderService).cancelOrder(ORDER_ID);

            ResponseEntity<HttpStatus> response = orderController.cancelOrder(ORDER_ID);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNull(response.getBody());
            verify(orderService).cancelOrder(ORDER_ID);
        }

        @Test
        @DisplayName("Should propagate NotFoundException when order does not exist")
        void cancelOrder_whenOrderNotFound_shouldPropagateNotFound() {
            doThrow(new NotFoundException("Order not found"))
                    .when(orderService).cancelOrder(ORDER_ID);

            NotFoundException ex = assertThrows(
                    NotFoundException.class,
                    () -> orderController.cancelOrder(ORDER_ID)
            );

            assertEquals("Order not found", ex.getMessage());
            verify(orderService).cancelOrder(ORDER_ID);
        }

        @Test
        @DisplayName("Should propagate AccessDeniedException when user has no rights to cancel the order")
        void cancelOrder_whenAccessDenied_shouldPropagateAccessDenied() {
            doThrow(new AccessDeniedException("Access denied"))
                    .when(orderService).cancelOrder(ORDER_ID);

            AccessDeniedException ex = assertThrows(
                    AccessDeniedException.class,
                    () -> orderController.cancelOrder(ORDER_ID)
            );

            assertEquals("Access denied", ex.getMessage());
            verify(orderService).cancelOrder(ORDER_ID);
        }
    }

    @Nested
    @DisplayName("confirmOrder() endpoint tests")
    class ConfirmOrderTests {

        @Test
        @DisplayName("Should return 200 OK and updated order when payment is successfully confirmed")
        void confirmOrder_whenOk_shouldReturnUpdatedOrder() {
            OrderResponseDto serviceResponse = new OrderResponseDto();
            when(orderService.confirmPayment(ORDER_ID, PAY_METHOD))
                    .thenReturn(serviceResponse);

            ResponseEntity<OrderResponseDto> response =
                    orderController.confirmOrder(ORDER_ID, PAY_METHOD);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertSame(serviceResponse, response.getBody());
            verify(orderService).confirmPayment(ORDER_ID, PAY_METHOD);
        }

        @Test
        @DisplayName("Should propagate BadRequestException when payment method is invalid")
        void confirmOrder_whenInvalidPayMethod_shouldPropagateBadRequest() {
            when(orderService.confirmPayment(ORDER_ID, PAY_METHOD))
                    .thenThrow(new BadRequestException("Invalid payment method"));

            BadRequestException ex = assertThrows(
                    BadRequestException.class,
                    () -> orderController.confirmOrder(ORDER_ID, PAY_METHOD)
            );

            assertEquals("Invalid payment method", ex.getMessage());
            verify(orderService).confirmPayment(ORDER_ID, PAY_METHOD);
        }

        @Test
        @DisplayName("Should propagate NotFoundException when order does not exist")
        void confirmOrder_whenOrderNotFound_shouldPropagateNotFound() {
            when(orderService.confirmPayment(ORDER_ID, PAY_METHOD))
                    .thenThrow(new NotFoundException("Order not found"));

            NotFoundException ex = assertThrows(
                    NotFoundException.class,
                    () -> orderController.confirmOrder(ORDER_ID, PAY_METHOD)
            );

            assertEquals("Order not found", ex.getMessage());
            verify(orderService).confirmPayment(ORDER_ID, PAY_METHOD);
        }

        @Test
        @DisplayName("Should propagate AccessDeniedException when user is not the owner of the order")
        void confirmOrder_whenAccessDenied_shouldPropagateAccessDenied() {
            when(orderService.confirmPayment(ORDER_ID, PAY_METHOD))
                    .thenThrow(new AccessDeniedException("Access denied"));

            AccessDeniedException ex = assertThrows(
                    AccessDeniedException.class,
                    () -> orderController.confirmOrder(ORDER_ID, PAY_METHOD)
            );

            assertEquals("Access denied", ex.getMessage());
            verify(orderService).confirmPayment(ORDER_ID, PAY_METHOD);
        }

        @Test
        @DisplayName("Should propagate MailSendingException when email cannot be sent")
        void confirmOrder_whenMailSendingFails_shouldPropagateMailSendingException() {
            when(orderService.confirmPayment(ORDER_ID, PAY_METHOD))
                    .thenThrow(new MailSendingException("Failed to send email"));

            MailSendingException ex = assertThrows(
                    MailSendingException.class,
                    () -> orderController.confirmOrder(ORDER_ID, PAY_METHOD)
            );

            assertEquals("Failed to send email", ex.getMessage());
            verify(orderService).confirmPayment(ORDER_ID, PAY_METHOD);
        }
    }

}