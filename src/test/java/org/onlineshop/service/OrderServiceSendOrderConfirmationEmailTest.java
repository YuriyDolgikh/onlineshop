package org.onlineshop.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.onlineshop.entity.Order;
import org.onlineshop.entity.User;
import org.onlineshop.exception.BadRequestException;
import org.onlineshop.exception.MailSendingException;
import org.onlineshop.repository.OrderRepository;
import org.onlineshop.service.mail.MailUtil;
import org.onlineshop.service.util.PdfOrderGenerator;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceSendOrderConfirmationEmailTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private MailUtil mailUtil;

    @InjectMocks
    private OrderService orderService;

    private Order createOrder() {
        User user = User.builder().userId(1).email("test@mail.com").build();
        return Order.builder().orderId(1).user(user).build();
    }

    @Test
    void sendOrderConfirmationEmailIfOK() {
        Integer orderId = 1;
        Order order = createOrder();
        byte[] pdfBytes = "pdf".getBytes();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        try (MockedStatic<PdfOrderGenerator> mockedStatic = mockStatic(PdfOrderGenerator.class)) {
            mockedStatic.when(() -> PdfOrderGenerator.generatePdfOrder(order)).thenReturn(pdfBytes);

            orderService.sendOrderConfirmationEmail(orderId);

            verify(mailUtil).sendOrderPaidEmail(order.getUser(), order, pdfBytes);
        }
    }

    @Test
    void sendOrderConfirmationEmailIfOrderNotFound() {
        Integer orderId = 1;
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class,
                () -> orderService.sendOrderConfirmationEmail(orderId));
    }

    @Test
    void sendOrderConfirmationEmailIfEmailFails() {
        Integer orderId = 1;
        Order order = createOrder();
        byte[] pdfBytes = "pdf".getBytes();

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        try (MockedStatic<PdfOrderGenerator> mockedStatic = mockStatic(PdfOrderGenerator.class)) {
            mockedStatic.when(() -> PdfOrderGenerator.generatePdfOrder(order)).thenReturn(pdfBytes);
            doThrow(new RuntimeException("SMTP error")).when(mailUtil).sendOrderPaidEmail(any(), any(), any());

            assertThrows(MailSendingException.class,
                    () -> orderService.sendOrderConfirmationEmail(orderId));
        }
    }
}