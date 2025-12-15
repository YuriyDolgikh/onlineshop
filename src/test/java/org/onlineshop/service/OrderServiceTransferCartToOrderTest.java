package org.onlineshop.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.onlineshop.entity.*;
import org.onlineshop.exception.BadRequestException;
import org.onlineshop.repository.CartRepository;
import org.onlineshop.repository.OrderRepository;
import org.onlineshop.repository.UserRepository;
import org.onlineshop.service.converter.CartItemConverter;
import org.onlineshop.service.interfaces.UserServiceInterface;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTransferCartToOrderTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserServiceInterface userService;

    @Mock
    private CartItemConverter cartItemConverter;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;

    private User userTest;
    private Cart cartTest;
    private CartItem cartItemTest;
    private OrderItem orderItemTest;

    @BeforeEach
    void setUp() {
        userTest = User.builder()
                .userId(1)
                .username("testUser")
                .email("test@test.com")
                .phoneNumber("+49787878787878")
                .status(User.Status.CONFIRMED)
                .role(User.Role.USER)
                .orders(new ArrayList<>())
                .build();

        cartTest = Cart.builder()
                .cartId(1)
                .user(userTest)
                .cartItems(new HashSet<>())
                .build();

        userTest.setCart(cartTest);

        Product productTest = Product.builder()
                .id(100)
                .name("Test Product")
                .price(BigDecimal.valueOf(10))
                .discountPrice(BigDecimal.valueOf(5))
                .build();

        cartItemTest = CartItem.builder()
                .cartItemId(50)
                .product(productTest)
                .cart(cartTest)
                .quantity(1)
                .build();

        cartTest.getCartItems().add(cartItemTest);

        orderItemTest = OrderItem.builder()
                .orderItemId(500)
                .product(productTest)
                .quantity(1)
                .priceAtPurchase(productTest.getPrice())
                .build();
    }

    @AfterEach
    void tearDown() {
        cartRepository.deleteAll();
        orderRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void testTransferCartToOrderAllIsOk() {
        when(userService.getCurrentUser()).thenReturn(userTest);
        when(cartRepository.findByUser(userTest)).thenReturn(Optional.of(cartTest));
        when(cartItemConverter.cartItemToOrderItem(cartItemTest)).thenReturn(orderItemTest);
        when(orderRepository.findByUserAndStatus(userTest, Order.Status.PENDING_PAYMENT)).thenReturn(null);

        Order savedOrder = Order.builder()
                .orderId(200)
                .user(userTest)
                .status(Order.Status.PENDING_PAYMENT)
                .deliveryMethod(Order.DeliveryMethod.PICKUP)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .orderItems(new ArrayList<>())
                .build();

        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

        orderService.transferCartToOrder();

        verify(userService, atLeastOnce()).getCurrentUser();
        verify(cartRepository, times(1)).findByUser(userTest);
        verify(cartItemConverter, times(1)).cartItemToOrderItem(cartItemTest);
        verify(orderRepository, times(2)).save(any(Order.class));
        verify(userService, times(1)).saveUser(userTest);
    }

    @Test
    void testTransferCartToOrderWithMultipleItems() {
        Product productTest2 = Product.builder()
                .id(101)
                .name("Test Product 2")
                .price(BigDecimal.valueOf(20))
                .discountPrice(BigDecimal.valueOf(15))
                .build();

        CartItem cartItemTest2 = CartItem.builder()
                .cartItemId(51)
                .product(productTest2)
                .cart(cartTest)
                .quantity(2)
                .build();

        cartTest.getCartItems().add(cartItemTest2);

        OrderItem orderItemTest2 = OrderItem.builder()
                .orderItemId(501)
                .product(productTest2)
                .quantity(2)
                .priceAtPurchase(productTest2.getPrice())
                .build();

        when(userService.getCurrentUser()).thenReturn(userTest);
        when(cartRepository.findByUser(userTest)).thenReturn(Optional.of(cartTest));
        when(cartItemConverter.cartItemToOrderItem(cartItemTest)).thenReturn(orderItemTest);
        when(cartItemConverter.cartItemToOrderItem(cartItemTest2)).thenReturn(orderItemTest2);
        when(orderRepository.findByUserAndStatus(userTest, Order.Status.PENDING_PAYMENT)).thenReturn(null);

        Order savedOrder = Order.builder()
                .orderId(200)
                .user(userTest)
                .status(Order.Status.PENDING_PAYMENT)
                .deliveryMethod(Order.DeliveryMethod.PICKUP)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .orderItems(new ArrayList<>())
                .build();

        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

        orderService.transferCartToOrder();

        verify(cartItemConverter, times(2)).cartItemToOrderItem(any(CartItem.class));
        verify(orderRepository, times(2)).save(any(Order.class));
        verify(userService, times(1)).saveUser(userTest);
    }

    @Test
    void testTransferCartToOrderWithEmptyCart() {
        cartTest.getCartItems().clear();

        when(userService.getCurrentUser()).thenReturn(userTest);
        when(cartRepository.findByUser(userTest)).thenReturn(Optional.of(cartTest));

        assertThrows(BadRequestException.class, () -> orderService.transferCartToOrder());

        verify(orderRepository, never()).save(any(Order.class));
        verify(userService, never()).saveUser(any(User.class));
    }

    @Test
    void testTransferCartToOrderWhenProductHasNoDiscount() {
        cartItemTest.getProduct().setDiscountPrice(null);

        when(userService.getCurrentUser()).thenReturn(userTest);
        when(cartRepository.findByUser(userTest)).thenReturn(Optional.of(cartTest));

        assertThrows(BadRequestException.class, () -> orderService.transferCartToOrder());

        verify(orderRepository, never()).save(any(Order.class));
        verify(userService, never()).saveUser(any(User.class));
    }

    @Test
    void testTransferCartToOrderWhenPendingOrderExists() {
        when(userService.getCurrentUser()).thenReturn(userTest);
        when(cartRepository.findByUser(userTest)).thenReturn(Optional.of(cartTest));

        Order existingOrder = Order.builder()
                .orderId(300)
                .user(userTest)
                .status(Order.Status.PENDING_PAYMENT)
                .build();
        when(orderRepository.findByUserAndStatus(userTest, Order.Status.PENDING_PAYMENT)).thenReturn(existingOrder);

        assertThrows(NullPointerException.class, () -> orderService.transferCartToOrder());

        verify(orderRepository, never()).save(any(Order.class));
        verify(userService, never()).saveUser(any(User.class));
    }
}