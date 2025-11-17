package org.onlineshop.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.onlineshop.dto.cart.CartResponseDto;
import org.onlineshop.dto.cartItem.CartItemResponseDto;
import org.onlineshop.dto.cartItem.CartItemSympleResponseDto;
import org.onlineshop.entity.Cart;
import org.onlineshop.entity.CartItem;
import org.onlineshop.entity.Product;
import org.onlineshop.entity.User;
import org.onlineshop.service.converter.CartItemConverter;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.yml")
class CartServiceGetCartFullDataTest {
    @MockBean
    private UserService userService;

    @SpyBean
    private CartService cartService;

    @MockBean
    private CartItemConverter cartItemConverter;

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

        userTest.setCart(cartTest);

        cartItemTest = CartItem.builder()
                .cart(cartTest)
                .product(productTest)
                .quantity(2)
                .build();

        cartTest.getCartItems().add(cartItemTest);

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
        doReturn(cartTest).when(cartService).getCurrentCart();

        when(cartItemConverter.toDto(cartItemTest))
                .thenReturn(cartItemDto);

        when(cartItemConverter.toSympleDtoFromDto(cartItemDto))
                .thenReturn(simpleDto);

        CartResponseDto result = cartService.getCartFullData();

        assertEquals(BigDecimal.valueOf(180), result.getTotalPrice());

        assertEquals(userTest.getUserId(), result.getUserId());

        assertEquals(1, result.getCartSympleItems().size());
        assertEquals("TestProduct", result.getCartSympleItems().get(0).getProductName());
        assertEquals(2, result.getCartSympleItems().get(0).getQuantity());

        verify(userService, times(1)).getCurrentUser();
        verify(cartService, times(1)).getCurrentCart();
        verify(cartItemConverter, times(1)).toDto(cartItemTest);
        verify(cartItemConverter, times(1)).toSympleDtoFromDto(cartItemDto);
    }
}