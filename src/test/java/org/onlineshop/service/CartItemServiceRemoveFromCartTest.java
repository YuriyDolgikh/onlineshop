package org.onlineshop.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.onlineshop.dto.cartItem.CartItemResponseDto;
import org.onlineshop.entity.Cart;
import org.onlineshop.entity.CartItem;
import org.onlineshop.entity.Product;
import org.onlineshop.exception.NotFoundException;
import org.onlineshop.repository.CartItemRepository;
import org.onlineshop.service.converter.CartItemConverter;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartItemServiceRemoveFromCartTest {

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private UserService userService;

    @Mock
    private CartItemConverter cartItemConverter;

    @Mock
    private CartService cartService;

    @Mock
    private ProductService productService;

    @InjectMocks
    private CartItemService cartItemService;

    private Cart testCart;
    private Product testProduct;
    private CartItem testCartItem;
    private CartItemResponseDto expectedResponseDto;

    @BeforeEach
    void setUp() {
        testProduct = new Product();
        testProduct.setId(1);
        testProduct.setName("Test Product");
        testProduct.setPrice(BigDecimal.valueOf(100));

        testCartItem = new CartItem();
        testCartItem.setProduct(testProduct);
        testCartItem.setQuantity(2);

        testCart = new Cart();
        Set<CartItem> cartItems = new HashSet<>();
        cartItems.add(testCartItem);
        testCart.setCartItems(cartItems);

        expectedResponseDto = new CartItemResponseDto();
        expectedResponseDto.setProduct(testProduct);
        expectedResponseDto.setQuantity(2);
    }

    @Test
    void testRemoveItemFromCartIfAllOk() {
        when(cartService.getCurrentCart()).thenReturn(testCart);
        when(cartItemConverter.toDto(testCartItem)).thenReturn(expectedResponseDto);

        CartItemResponseDto result = cartItemService.removeItemFromCart(1);

        assertNotNull(result);
        assertEquals(testProduct, result.getProduct());
        assertEquals(2, result.getQuantity());
        assertTrue(testCart.getCartItems().isEmpty());

        verify(cartItemRepository).delete(testCartItem);
        verify(cartService).saveCart(testCart);
    }

    @Test
    void testRemoveItemFromCartWhenProductNotInCart() {
        when(cartService.getCurrentCart()).thenReturn(testCart);

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> cartItemService.removeItemFromCart(999));

        assertEquals("Product with ID: 999 not found in cart", exception.getMessage());
        verify(cartItemRepository, never()).delete(any());
        verify(cartService, never()).saveCart(any());
    }

    @Test
    void testRemoveItemFromCartWhenProductIdIsNull() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> cartItemService.removeItemFromCart(null));

        assertEquals("Product Id cannot be null", exception.getMessage());
        verifyNoInteractions(cartService, cartItemRepository, cartItemConverter);
    }

    @Test
    void testRemoveItemFromCartWhenCartIsEmpty() {
        testCart.setCartItems(new HashSet<>());
        when(cartService.getCurrentCart()).thenReturn(testCart);

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> cartItemService.removeItemFromCart(1));

        assertEquals("Product with ID: 1 not found in cart", exception.getMessage());
        verify(cartItemRepository, never()).delete(any());
        verify(cartService, never()).saveCart(any());
    }
}