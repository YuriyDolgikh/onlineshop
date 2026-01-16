package org.onlineshop.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.onlineshop.entity.Order;
import org.onlineshop.entity.OrderItem;
import org.onlineshop.entity.Product;
import org.onlineshop.exception.BadRequestException;
import org.onlineshop.exception.NotFoundException;
import org.onlineshop.repository.OrderRepository;
import org.onlineshop.service.util.PriceCalculator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceRecalculateOrderPriceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private PriceCalculator priceCalculator;

    @InjectMocks
    private OrderService orderService;

    private Order order;
    private Product product1;
    private Product product2;
    private List<OrderItem> orderItems;

    @BeforeEach
    void setUp() {
        product1 = new Product();
        product1.setPrice(new BigDecimal("100.00"));
        product1.setDiscountPrice(new BigDecimal("10.00"));

        product2 = new Product();
        product2.setPrice(new BigDecimal("200.00"));
        product2.setDiscountPrice(new BigDecimal("20.00"));

        OrderItem item1 = new OrderItem();
        item1.setProduct(product1);
        item1.setPriceAtPurchase(BigDecimal.ZERO);

        OrderItem item2 = new OrderItem();
        item2.setProduct(product2);
        item2.setPriceAtPurchase(BigDecimal.ZERO);

        orderItems = new ArrayList<>();
        orderItems.add(item1);
        orderItems.add(item2);

        order = new Order();
        order.setStatus(Order.Status.PENDING_PAYMENT);
        order.setOrderItems(orderItems);
    }

    @Test
    void recalculateOrderPrice_SuccessWithDiscounts() {
        when(priceCalculator.calculateDiscountedPrice(
                eq(new BigDecimal("100.00")), eq(new BigDecimal("10.00"))))
                .thenReturn(new BigDecimal("90.00"));
        when(priceCalculator.calculateDiscountedPrice(
                eq(new BigDecimal("200.00")), eq(new BigDecimal("20.00"))))
                .thenReturn(new BigDecimal("160.00"));

        when(orderRepository.save(any(Order.class))).thenReturn(order);

        orderService.recalculateOrderPrice(order);

        assertEquals(new BigDecimal("90.00"), orderItems.get(0).getPriceAtPurchase());
        assertEquals(new BigDecimal("160.00"), orderItems.get(1).getPriceAtPurchase());
        verify(orderRepository, times(1)).save(order);
        verify(priceCalculator, times(1))
                .calculateDiscountedPrice(new BigDecimal("100.00"), new BigDecimal("10.00"));
        verify(priceCalculator, times(1))
                .calculateDiscountedPrice(new BigDecimal("200.00"), new BigDecimal("20.00"));
    }

    @Test
    void recalculateOrderPrice_SuccessWithZeroDiscount() {
        product1.setDiscountPrice(null);

        when(priceCalculator.calculateDiscountedPrice(
                eq(new BigDecimal("100.00")), eq(BigDecimal.ZERO)))
                .thenReturn(new BigDecimal("100.00"));
        when(priceCalculator.calculateDiscountedPrice(
                eq(new BigDecimal("200.00")), eq(new BigDecimal("20.00"))))
                .thenReturn(new BigDecimal("160.00"));

        when(orderRepository.save(any(Order.class))).thenReturn(order);

        orderService.recalculateOrderPrice(order);

        assertEquals(new BigDecimal("100.00"), orderItems.get(0).getPriceAtPurchase());
        assertEquals(new BigDecimal("160.00"), orderItems.get(1).getPriceAtPurchase());
        verify(orderRepository, times(1)).save(order);
    }

    @Test
    void recalculateOrderPrice_ThrowsBadRequestException_WhenOrderIsNull() {
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> orderService.recalculateOrderPrice(null));

        assertEquals("Order cannot be null", exception.getMessage());
        verify(orderRepository, never()).save(any());
        verify(priceCalculator, never()).calculateDiscountedPrice(any(), any());
    }

    @Test
    void recalculateOrderPrice_ThrowsBadRequestException_WhenStatusNotPendingPayment() {
        order.setStatus(Order.Status.PROCESSING);

        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> orderService.recalculateOrderPrice(order));

        assertEquals("The price can't be recalculated for the order not in PENDING_PAYMENT status",
                exception.getMessage());
        verify(orderRepository, never()).save(any());
        verify(priceCalculator, never()).calculateDiscountedPrice(any(), any());
    }

    @Test
    void recalculateOrderPrice_ThrowsNotFoundException_WhenOrderItemsIsNull() {
        order.setOrderItems(null);

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> orderService.recalculateOrderPrice(order));

        assertEquals("Order Items cannot be null or empty", exception.getMessage());
        verify(orderRepository, never()).save(any());
        verify(priceCalculator, never()).calculateDiscountedPrice(any(), any());
    }

    @Test
    void recalculateOrderPrice_ThrowsNotFoundException_WhenOrderItemsIsEmpty() {
        order.setOrderItems(new ArrayList<>());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> orderService.recalculateOrderPrice(order));

        assertEquals("Order Items cannot be null or empty", exception.getMessage());
        verify(orderRepository, never()).save(any());
        verify(priceCalculator, never()).calculateDiscountedPrice(any(), any());
    }

    @Test
    void recalculateOrderPrice_HandlesZeroDiscountPrice() {
        product1.setDiscountPrice(BigDecimal.ZERO);

        when(priceCalculator.calculateDiscountedPrice(
                eq(new BigDecimal("100.00")), eq(BigDecimal.ZERO)))
                .thenReturn(new BigDecimal("100.00"));
        when(priceCalculator.calculateDiscountedPrice(
                eq(new BigDecimal("200.00")), eq(new BigDecimal("20.00"))))
                .thenReturn(new BigDecimal("160.00"));

        when(orderRepository.save(any(Order.class))).thenReturn(order);

        orderService.recalculateOrderPrice(order);

        assertEquals(new BigDecimal("100.00"), orderItems.get(0).getPriceAtPurchase());
        assertEquals(new BigDecimal("160.00"), orderItems.get(1).getPriceAtPurchase());
        verify(orderRepository, times(1)).save(order);
    }

    @Test
    void recalculateOrderPrice_CallsPriceCalculatorWithCorrectParameters() {
        when(priceCalculator.calculateDiscountedPrice(
                eq(new BigDecimal("100.00")), eq(new BigDecimal("10.00"))))
                .thenReturn(new BigDecimal("90.00"));
        when(priceCalculator.calculateDiscountedPrice(
                eq(new BigDecimal("200.00")), eq(new BigDecimal("20.00"))))
                .thenReturn(new BigDecimal("160.00"));

        when(orderRepository.save(any(Order.class))).thenReturn(order);

        orderService.recalculateOrderPrice(order);

        verify(priceCalculator, times(1))
                .calculateDiscountedPrice(new BigDecimal("100.00"), new BigDecimal("10.00"));
        verify(priceCalculator, times(1))
                .calculateDiscountedPrice(new BigDecimal("200.00"), new BigDecimal("20.00"));
    }

    @Test
    void recalculateOrderPrice_SetsPriceAtPurchaseInOrderItems() {
        BigDecimal expectedPrice1 = new BigDecimal("90.00");
        BigDecimal expectedPrice2 = new BigDecimal("160.00");

        when(priceCalculator.calculateDiscountedPrice(any(), any()))
                .thenReturn(expectedPrice1)
                .thenReturn(expectedPrice2);

        when(orderRepository.save(any(Order.class))).thenReturn(order);

        orderService.recalculateOrderPrice(order);

        assertEquals(expectedPrice1, orderItems.get(0).getPriceAtPurchase());
        assertEquals(expectedPrice2, orderItems.get(1).getPriceAtPurchase());
    }
}