package org.onlineshop.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.onlineshop.entity.Cart;
import org.onlineshop.entity.CartItem;
import org.onlineshop.entity.Product;
import org.onlineshop.entity.User;
import org.onlineshop.exception.BadRequestException;
import org.onlineshop.repository.CartRepository;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceGetCurrentCartTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private UserService userService;

    @InjectMocks
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

        verify(userService).getCurrentUser();
        verify(cartRepository).findByUser(userTest);
    }

    @Test
    void testGetCurrentCartIfEmpty() {
        when(userService.getCurrentUser()).thenReturn(userTest);
        when(cartRepository.findByUser(userTest)).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class, () -> cartService.getCurrentCart());
    }
}
