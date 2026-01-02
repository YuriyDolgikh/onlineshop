package org.onlineshop.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.onlineshop.dto.cartItem.CartItemFullResponseDto;
import org.onlineshop.service.CartItemService;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CartItemControllerGetCartItemsTest {

    @Mock
    private CartItemService cartItemService;

    @InjectMocks
    private CartItemController cartItemController;

    @Test
    void getCartItemsIfOk() {
        Set<CartItemFullResponseDto> expectedSet = new LinkedHashSet<>();
        for (int i = 0; i < 5; i++) {
            expectedSet.add(CartItemFullResponseDto.builder()
                    .cartItemId(1 + i)
                    .productName("Product " + i)
                    .categoryName("Category")
                    .productPrice(new BigDecimal("100.00").add(new BigDecimal(i + 20)))
                    .productDiscountPrice(new BigDecimal("15.00").subtract(new BigDecimal(i)))
                    .quantity(2 + i)
                    .build());
        }

        when(cartItemService.getCartItems()).thenReturn(expectedSet);

        ResponseEntity<Set<CartItemFullResponseDto>> response = cartItemController.getCartItems();

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(expectedSet.size(), response.getBody().size());

        CartItemFullResponseDto firstItem = response.getBody().iterator().next();
        assertEquals("Product 0", firstItem.getProductName());
        assertEquals(1, firstItem.getCartItemId());
        assertEquals("Category", firstItem.getCategoryName());
        assertEquals(new BigDecimal("120.00"), firstItem.getProductPrice());
        assertEquals(new BigDecimal("15.00"), firstItem.getProductDiscountPrice());
        assertEquals(2, firstItem.getQuantity());
    }

    @Test
    void getCartItemsIfCartEmpty() {
        Set<CartItemFullResponseDto> emptySet = new LinkedHashSet<>();
        when(cartItemService.getCartItems()).thenReturn(emptySet);

        ResponseEntity<Set<CartItemFullResponseDto>> response = cartItemController.getCartItems();

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
    }
}