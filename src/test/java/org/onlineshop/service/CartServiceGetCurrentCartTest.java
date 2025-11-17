package org.onlineshop.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.onlineshop.entity.Cart;
import org.onlineshop.entity.CartItem;
import org.onlineshop.entity.Product;
import org.onlineshop.entity.User;
import org.onlineshop.exception.BadRequestException;
import org.onlineshop.repository.CartRepository;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.yml")
class CartServiceGetCurrentCartTest {
    @MockBean
    private CartRepository cartRepository;

    @MockBean
    private UserService userService;

    @SpyBean
    private CartService cartService;

    private User userTest;

    private Cart cartTest;

    private Product productTest;

    private CartItem cartItemTest;

    @BeforeEach
    void setUp() {

        userTest = User.builder()
                .username("user")
                .email("user@mail.com")
                .build();

        productTest = Product.builder()
                .name("TestProduct")
                .price(BigDecimal.valueOf(100))
                .discountPrice(BigDecimal.valueOf(10))
                .build();

        cartTest = Cart.builder()
                .user(userTest)
                .cartItems(new HashSet<>())
                .build();

        userTest.setCart(cartTest);

        cartItemTest = CartItem.builder()
                .cart(cartTest)
                .product(productTest)
                .quantity(2)
                .build();

        cartTest.getCartItems().add(cartItemTest);

    }

    @Test
    void testGetCurrentCart() {
        when(userService.getCurrentUser()).thenReturn(userTest);
        when(cartRepository.findByUser(userTest)).thenReturn(Optional.of(cartTest));

        Cart result = cartService.getCurrentCart();

        assertNotNull(result);
        assertEquals(cartTest, result);

        verify(userService, times(1)).getCurrentUser();
        verify(cartRepository, times(1)).findByUser(userTest);
    }

    @Test
    void testGetCurrentCartIfEmpty() {
        when(userService.getCurrentUser()).thenReturn(userTest);
        when(cartRepository.findByUser(userTest)).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class, () -> cartService.getCurrentCart());
    }

}