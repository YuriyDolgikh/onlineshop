package org.onlineshop.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.onlineshop.entity.Cart;
import org.onlineshop.entity.CartItem;
import org.onlineshop.entity.User;
import org.onlineshop.repository.CartRepository;
import org.onlineshop.service.converter.CartItemConverter;
import org.onlineshop.service.interfaces.UserServiceInterface;

import java.util.HashSet;
import java.util.Set;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CartServiceClearCartTest {

    @Mock
    private UserServiceInterface userService;

    @Mock
    private CartItemConverter cartItemConverter;

    @Mock
    private CartRepository cartRepository;

    @InjectMocks
    private CartService cartService;

    private User userTest;
    private Cart cartTest;
    private Set<CartItem> cartItemsTest;

    @BeforeEach
    void setUp() {
        userTest = User.builder()
                .userId(1)
                .username("testUser")
                .email("test@email.com")
                .hashPassword("hashedPassword")
                .phoneNumber("+1234567890")
                .status(User.Status.CONFIRMED)
                .role(User.Role.USER)
                .build();

        cartItemsTest = new HashSet<>();
        cartItemsTest.add(new CartItem());
        cartItemsTest.add(new CartItem());

        cartTest = Cart.builder()
                .user(userTest)
                .cartItems(cartItemsTest)
                .build();

        userTest.setCart(cartTest);
    }

    @Test
    void testClearCartSuccess() {
        when(userService.getCurrentUser()).thenReturn(userTest);
        when(userService.saveUser(userTest)).thenReturn(userTest);

        cartService.clearCart();

        verify(userService).getCurrentUser();
        verify(userService).saveUser(userTest);
        assert cartTest.getCartItems().isEmpty();
    }

    @Test
    void testClearCartWhenCartItemsAlreadyEmpty() {
        cartItemsTest.clear();
        when(userService.getCurrentUser()).thenReturn(userTest);
        when(userService.saveUser(userTest)).thenReturn(userTest);

        cartService.clearCart();

        verify(userService).getCurrentUser();
        verify(userService).saveUser(userTest);
        assert cartTest.getCartItems().isEmpty();
    }
}