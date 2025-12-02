package org.onlineshop.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.onlineshop.entity.Cart;
import org.onlineshop.entity.User;
import org.onlineshop.repository.CartRepository;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CartServiceSaveCartTest {

    @Mock
    private CartRepository cartRepository;

    @InjectMocks
    private CartService cartService;

    private User userTest;

    private Cart cartTest;

    @BeforeEach
    void setUp() {
        userTest = User.builder()
                .username("newTestUser")
                .email("testUser@email.com")
                .hashPassword("$2a$10$WiAt7dmC1vLIxjY9/9n7P.I5RQU1MKKSOI1Dy1pNLPPIts7K5RJR2")
                .phoneNumber("+494949494949")
                .status(User.Status.CONFIRMED)
                .role(User.Role.USER)
                .build();

        cartTest = Cart.builder()
                .user(userTest)
                .cartItems(new HashSet<>())
                .build();

    }

    @Test
    void testSaveCartIfOk() {
        when(cartRepository.save(cartTest)).thenReturn(cartTest);

        Cart savedCart = cartService.saveCart(cartTest);

        assertNotNull(savedCart);
        verify(cartRepository).save(cartTest);

    }

    @Test
    void testSaveCartIfCartNull() {
        assertThrows(IllegalArgumentException.class, () -> cartService.saveCart(null));
    }

    @Test
    void testSaveCartIfUserNull() {
        Cart cart = Cart.builder()
                .cartItems(new HashSet<>())
                .build();
        assertThrows(IllegalArgumentException.class, () -> cartService.saveCart(cart));
    }
}