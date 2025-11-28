package org.onlineshop.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.onlineshop.dto.cartItem.CartItemFullResponseDto;
import org.onlineshop.service.CartItemService;
import org.springframework.http.ResponseEntity;

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
                    .productPrice(100.0 + (i + 20))
                    .productDiscountPrice(15.0 - i)
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
        assertEquals(120.0, firstItem.getProductPrice());
        assertEquals(15.0, firstItem.getProductDiscountPrice());
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