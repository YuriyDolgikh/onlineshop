package org.onlineshop.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.onlineshop.dto.cartItem.CartItemResponseDto;
import org.onlineshop.entity.Product;
import org.onlineshop.exception.NotFoundException;
import org.onlineshop.service.CartItemService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartItemControllerDeleteCartItemTest {

    @Mock
    private CartItemService cartItemService;

    @InjectMocks
    private CartItemController cartItemController;

    @Test
    void deleteCartItemIfExists() {
        Product product = new Product();
        product.setId(123);
        product.setName("Test Product");
        product.setPrice(new BigDecimal("100.0"));

        CartItemResponseDto response = new CartItemResponseDto();
        response.setProduct(product);
        response.setQuantity(2);

        when(cartItemService.removeItemFromCart(123)).thenReturn(response);

        ResponseEntity<CartItemResponseDto> result = cartItemController.deleteCartItem(123);

        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals("Test Product", result.getBody().getProduct().getName());
        assertEquals(2, result.getBody().getQuantity());
        verify(cartItemService, times(1)).removeItemFromCart(123);
    }

    @Test
    void deleteCartItemIfNotFound() {
        doThrow(new NotFoundException("Product not found in cart"))
                .when(cartItemService).removeItemFromCart(999);

        assertThrows(NotFoundException.class, () -> cartItemController.deleteCartItem(999));
        verify(cartItemService, times(1)).removeItemFromCart(999);
    }
}