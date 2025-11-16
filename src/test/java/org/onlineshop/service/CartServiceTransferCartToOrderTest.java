package org.onlineshop.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.onlineshop.entity.*;
import org.onlineshop.repository.CartRepository;
import org.onlineshop.repository.OrderRepository;
import org.onlineshop.service.converter.CartItemConverter;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
@MockitoSettings(strictness = Strictness.LENIENT)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.yml")
class CartServiceTransferCartToOrderTest {

    @MockBean
    private CartRepository cartRepository;

    @MockBean
    private UserService userService;

    @MockBean
    private CartItemConverter cartItemConverter;

    @MockBean
    private OrderRepository orderRepository;

    @SpyBean
    private CartService cartService;

    private User userTest;

    private Cart cartTest;

    private Product productTest;

    private CartItem cartItemTest;

    private OrderItem orderItemTest;

    static class TestOrder extends Order {
        @Override
        public String toString() {
            return "TestOrder{id=" + getOrderId() + "}";
        }
    }

    static class TestOrderItem extends OrderItem {
        @Override
        public String toString() {
            return "TestOrderItem{id=" + getOrderItemId() + "}";
        }
    }

    @BeforeEach
    void setUp() {

        userTest = User.builder()
                .username("testUser")
                .email("test@test.com")
                .phoneNumber("+49787878787878")
                .status(User.Status.CONFIRMED)
                .role(User.Role.USER)
                .orders(new ArrayList<>())
                .build();

        cartTest = Cart.builder()
                .user(userTest)
                .cartItems(new HashSet<>())
                .build();

        userTest.setCart(cartTest);

        productTest = Product.builder()
                .name("Test Product")
                .price(BigDecimal.TEN)
                .build();

        cartItemTest = CartItem.builder()
                .product(productTest)
                .cart(cartTest)
                .quantity(1)
                .build();

        cartTest.getCartItems().add(cartItemTest);

        orderItemTest = new TestOrderItem();
        orderItemTest.setOrderItemId(500);
        orderItemTest.setQuantity(1);
    }

    // ---------- ТЕСТ ----------
    @Test
    void testTransferCartToOrder() {

        when(userService.getCurrentUser())
                .thenReturn(userTest);

        doReturn(cartTest)
                .when(cartService)
                .getCurrentCart();

        when(cartItemConverter.cartItemToOrderItem(cartItemTest))
                .thenReturn(orderItemTest);

        TestOrder savedOrder = new TestOrder();
        savedOrder.setOrderId(200);

        when(orderRepository.save(any(Order.class)))
                .thenReturn(savedOrder);

        cartService.transferCartToOrder();

        verify(orderRepository, times(2)).save(any(Order.class));
        verify(userService, times(2)).saveUser(userTest);
        verify(cartService, times(1)).clearCart();
    }
}