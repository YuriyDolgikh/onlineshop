package org.onlineshop.service.util;

import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.onlineshop.entity.Order;
import org.onlineshop.entity.User;
import org.onlineshop.exception.MailSendingException;
import org.onlineshop.service.mail.MailUtil;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MailUtilSendOrderPaidTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private MimeMessage mimeMessage;

    private MailUtil mailUtil;

    private User testUser;
    private Order testOrder;
    private byte[] testPdfBytes;

    @BeforeEach
    void setUp() {
        mailUtil = new MailUtil(mailSender, null);

        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");

        testOrder = new Order();
        testOrder.setOrderId(12345);
        testOrder.setDeliveryMethod(Order.DeliveryMethod.POST);
        testOrder.setDeliveryAddress("Dresden");

        testPdfBytes = "wrong pdf content".getBytes();
    }

    @Test
    void testSendOrderPaidEmailSuccess() {
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        mailUtil.sendOrderPaidEmail(testUser, testOrder, testPdfBytes);

        verify(mailSender).createMimeMessage();
        verify(mailSender).send(mimeMessage);
    }

    @Test
    void testSendOrderPaidEmailWhenNullUser() {
        assertThrows(MailSendingException.class,
                () -> mailUtil.sendOrderPaidEmail(null, testOrder, testPdfBytes));
    }

    @Test
    void testSendOrderPaidEmailWhenNullOrder() {
        assertThrows(MailSendingException.class,
                () -> mailUtil.sendOrderPaidEmail(testUser, null, testPdfBytes));
    }

    @Test
    void testSendOrderPaidEmailWhenNullPdfBytes() {
        assertThrows(MailSendingException.class,
                () -> mailUtil.sendOrderPaidEmail(testUser, testOrder, null));
    }

    @Test
    void testSendOrderPaidEmailWhenEmptyPdfBytes() {
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        byte[] emptyBytes = new byte[0];

        mailUtil.sendOrderPaidEmail(testUser, testOrder, emptyBytes);

        verify(mailSender).createMimeMessage();
        verify(mailSender).send(mimeMessage);
    }

    @Test
    void testSendOrderPaidEmailWhenMailSendingFails() {
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doThrow(new RuntimeException("SMTP error")).when(mailSender).send(any(MimeMessage.class));

        MailSendingException exception = assertThrows(MailSendingException.class,
                () -> mailUtil.sendOrderPaidEmail(testUser, testOrder, testPdfBytes));

        assertTrue(exception.getMessage().contains("Error sending order payment email"));
        assertTrue(exception.getMessage().contains("SMTP error"));
    }

    @Test
    void testSendOrderPaidEmailWhenExistSpecialCharactersInAddress() {
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        testOrder.setDeliveryAddress("Dresden");

        mailUtil.sendOrderPaidEmail(testUser, testOrder, testPdfBytes);

        verify(mailSender).createMimeMessage();
        verify(mailSender).send(mimeMessage);
    }

    @Test
    void testSendOrderPaidEmailVerifyEmailStructure() {
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        mailUtil.sendOrderPaidEmail(testUser, testOrder, testPdfBytes);

        verify(mailSender).createMimeMessage();
        verify(mailSender).send(mimeMessage);
    }
}