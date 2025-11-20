package org.onlineshop.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.onlineshop.dto.cartItem.CartItemFullResponseDto;
import org.onlineshop.entity.Cart;
import org.onlineshop.entity.CartItem;
import org.onlineshop.entity.Product;

import org.onlineshop.service.converter.CartItemConverter;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class CartItemServiceGetCartItemsTest {

    private CartItemService cartItemService;
    private CartService cartService;
    private CartItemConverter cartItemConverter;

    private Cart testCart;

    @BeforeEach
    void setUp() {
        cartService = mock(CartService.class);
        cartItemConverter = mock(CartItemConverter.class);

        cartItemService = new CartItemService(null, null, cartItemConverter, cartService, null);

        testCart = new Cart();

        Product p1 = new Product();
        p1.setId(1);
        p1.setName("Product1");

        Product p2 = new Product();
        p2.setId(2);
        p2.setName("Product2");

        CartItem ci1 = new CartItem();
        ci1.setProduct(p1);
        ci1.setQuantity(1);

        CartItem ci2 = new CartItem();
        ci2.setProduct(p2);
        ci2.setQuantity(2);

        Set<CartItem> cartItems = new HashSet<>();
        cartItems.add(ci1);
        cartItems.add(ci2);

        testCart.setCartItems(cartItems);

        when(cartService.getCurrentCart()).thenReturn(testCart);

        when(cartItemConverter.toFullDtos(any())).thenAnswer(invocation -> {
            Set<CartItem> items = invocation.getArgument(0);
            Set<CartItemFullResponseDto> dtos = new HashSet<>();
            for (CartItem ci : items) {
                CartItemFullResponseDto dto = new CartItemFullResponseDto();
                dto.setProductName(ci.getProduct().getName());
                dto.setQuantity(ci.getQuantity());
                dtos.add(dto);
            }
            return dtos;
        });
    }

    @Test
    void getCartItemsReturnsCorrectDtos() {
        Set<CartItemFullResponseDto> response = cartItemService.getCartItems();

        assertEquals(2, response.size(), "Should return 2 items");

        boolean p1Found = response.stream()
                .anyMatch(dto -> dto.getProductName()
                        .equals("Product1") && dto.getQuantity() == 1);
        boolean p2Found = response.stream()
                .anyMatch(dto -> dto.getProductName()
                        .equals("Product2") && dto.getQuantity() == 2);

        assertTrue(p1Found, "Product1 should be present");
        assertTrue(p2Found, "Product2 should be present");

        verify(cartService, times(1)).getCurrentCart();
        verify(cartItemConverter, times(1)).toFullDtos(testCart.getCartItems());
    }
}
