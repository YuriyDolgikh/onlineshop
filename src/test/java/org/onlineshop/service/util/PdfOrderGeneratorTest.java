package org.onlineshop.service.util;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.jupiter.api.Test;
import org.onlineshop.entity.Order;
import org.onlineshop.entity.OrderItem;
import org.onlineshop.entity.Product;
import org.onlineshop.entity.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PdfOrderGeneratorTest {

    @Test
    void generatePdfOrder_ShouldReturnPdfBytes() throws Exception {
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");

        Product product = new Product();
        product.setName("Laptop");
        product.setPrice(BigDecimal.valueOf(1000));
        product.setDiscountPrice(BigDecimal.valueOf(10));

        OrderItem item = new OrderItem();
        item.setProduct(product);
        item.setQuantity(2);

        Order order = new Order();
        order.setOrderId(1);
        order.setUser(user);
        order.setStatus(Order.Status.valueOf("PAID"));
        order.setDeliveryMethod(Order.DeliveryMethod.valueOf("COURIER"));
        order.setDeliveryAddress("Berlin, Street 123");
        order.setContactPhone("+491234567");
        order.setCreatedAt(LocalDateTime.of(2025, 1, 1, 12, 0));
        order.setOrderItems(List.of(item));

        byte[] pdfBytes = PdfOrderGenerator.generatePdfOrder(order);

        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 100); // PDF точно не пустой

        try (PDDocument document = PDDocument.load(pdfBytes)) {
            String text = new PDFTextStripper().getText(document);

            text = text.replaceAll("\\s+", " ");

            assertTrue(text.contains("Order Confirmation"), text);
            assertTrue(text.contains("Order ID: 1"), text);
            assertTrue(text.contains("testuser"), text);
            assertTrue(text.contains("test@example.com"), text);
            assertTrue(text.contains("Laptop"), text);
            assertTrue(text.contains("Total amount"), text);
            assertTrue(text.contains("1800"), text);
        }
    }

    @Test
    void generatePdfOrder_ShouldThrowException_WhenOrderIsNull() {
        RuntimeException exception =
                assertThrows(RuntimeException.class, () -> PdfOrderGenerator.generatePdfOrder(null));

        assertTrue(exception.getMessage().contains("Error generating PDF order"));
    }
}
