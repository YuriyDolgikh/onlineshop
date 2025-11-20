package org.onlineshop.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.onlineshop.dto.cart.CartResponseDto;
import org.onlineshop.dto.cartItem.CartItemResponseDto;
import org.onlineshop.dto.cartItem.CartItemSympleResponseDto;
import org.onlineshop.entity.Cart;
import org.onlineshop.entity.CartItem;
import org.onlineshop.entity.Product;
import org.onlineshop.entity.User;
import org.onlineshop.repository.CartRepository;
import org.onlineshop.service.converter.CartItemConverter;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceGetCartFullDataTest {

    @Mock
    private UserService userService;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemConverter cartItemConverter;

    @InjectMocks
    private CartService cartService;

    private User userTest;
    private Cart cartTest;
    private Product productTest;
    private CartItem cartItemTest;
    private CartItemResponseDto cartItemDto;
    private CartItemSympleResponseDto simpleDto;

    @BeforeEach
    void setUp() {

        userTest = User.builder()
                .username("user")
                .email("user@mail.com")
                .phoneNumber("+497856221413")
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

        cartItemTest = CartItem.builder()
                .cart(cartTest)
                .product(productTest)
                .quantity(2)
                .build();

        cartTest.getCartItems().add(cartItemTest);
        userTest.setCart(cartTest);

        cartItemDto = CartItemResponseDto.builder()
                .product(productTest)
                .quantity(2)
                .build();

        simpleDto = CartItemSympleResponseDto.builder()
                .productName("TestProduct")
                .quantity(2)
                .build();
    }

    @Test
    void testGetCartFullData() {
        when(userService.getCurrentUser()).thenReturn(userTest);
        when(cartRepository.findByUser(userTest)).thenReturn(Optional.of(cartTest));
        when(cartItemConverter.toDto(cartItemTest)).thenReturn(cartItemDto);
        when(cartItemConverter.toSympleDtoFromDto(cartItemDto)).thenReturn(simpleDto);

        CartResponseDto result = cartService.getCartFullData();

        assertEquals(BigDecimal.valueOf(180), result.getTotalPrice());
        assertEquals(userTest.getUserId(), result.getUserId());

        assertEquals(1, result.getCartSympleItems().size());
        assertEquals("TestProduct", result.getCartSympleItems().get(0).getProductName());
        assertEquals(2, result.getCartSympleItems().get(0).getQuantity());

        verify(userService, times(2)).getCurrentUser();
        verify(cartRepository).findByUser(userTest);
        verify(cartItemConverter).toDto(cartItemTest);
        verify(cartItemConverter).toSympleDtoFromDto(cartItemDto);
    }
}
