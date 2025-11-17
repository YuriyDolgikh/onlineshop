package org.onlineshop.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.onlineshop.dto.cartItem.CartItemRequestDto;
import org.onlineshop.dto.cartItem.CartItemSympleResponseDto;
import org.onlineshop.entity.Cart;
import org.onlineshop.entity.CartItem;
import org.onlineshop.entity.Product;
import org.onlineshop.entity.User;
import org.onlineshop.exception.BadRequestException;
import org.onlineshop.repository.CartItemRepository;
import org.onlineshop.service.converter.CartItemConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.AssertionErrors.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.yml")
class CartItemServiceAddItemToCartTest {

    @Autowired
    private CartItemService cartItemService;
    @MockBean
    private UserService userService;
    @MockBean
    private ProductService productService;
    @MockBean
    private CartItemRepository cartItemRepository;
    @MockBean
    private CartService cartService;
    @MockBean
    private CartItemConverter cartItemConverter;

    @BeforeEach
    void setUp() {
        User testUser = new User();
        Cart testCart = new Cart();
        testUser.setCart(testCart);

        Product product1 = new Product();
        product1.setId(1);
        CartItem cartItem1 = new CartItem();
        cartItem1.setProduct(product1);

        Product product2 = new Product();
        product2.setId(2);
        CartItem cartItem2 = new CartItem();
        cartItem2.setProduct(product2);

        Set<CartItem> cartItems = new HashSet<>();
        cartItems.add(cartItem1);
        cartItems.add(cartItem2);

        testCart.setCartItems(cartItems);

        when(userService.getCurrentUser()).thenReturn(testUser);
    }

    @Test
    void addItemToCartReturnsCorrectDto() {
        Product product = new Product();
        product.setId(5);
        product.setName("Test Product");
        product.setPrice(new BigDecimal("100.0"));

        CartItem existingItem = CartItem.builder()
                .product(product)
                .quantity(2)
                .build();
        Cart cart = new Cart();
        cart.setCartItems(new HashSet<>(Set.of(existingItem)));
        cart.getCartItems().add(existingItem);

        User user = new User();
        user.setCart(cart);
        when(userService.getCurrentUser()).thenReturn(user);
        when(productService.getProductById(5)).thenReturn(Optional.of(product));
        when(cartItemRepository.save(any(CartItem.class))).thenAnswer(i -> i.getArgument(0));
        when(cartItemConverter.toSympleDto(any(CartItem.class))).thenAnswer(i -> {
            CartItem item = i.getArgument(0);
            CartItemSympleResponseDto dto = new CartItemSympleResponseDto();
            dto.setQuantity(item.getQuantity());
            if (item.getProduct() != null) {
                dto.setProductName(item.getProduct().getName());
            }
            return dto;
        });

        CartItemRequestDto request = new CartItemRequestDto(5, 3);
        CartItemSympleResponseDto response = cartItemService.addItemToCart(request);

        assertEquals("Quantity should be 5", 5, response.getQuantity());
        org.junit.jupiter.api.Assertions.assertEquals("Test Product", response.getProductName(), "Product name should match");

        verify(userService, times(2)).getCurrentUser();
        verify(productService, times(1)).getProductById(5);
        verify(cartItemRepository, times(1)).save(existingItem);
        verify(cartItemConverter, times(1)).toSympleDto(existingItem);
    }

    @Test
    void addItemToCartThrowsIfProductIdNull() {
        CartItemRequestDto request = new CartItemRequestDto(null, 2);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> cartItemService.addItemToCart(request));

        assertEquals("Product Id cannot be null", exception.getMessage(), "Product Id cannot be null");
    }

    @Test
    void addItemToCartThrowsIfQuantityNull() {
        CartItemRequestDto request = new CartItemRequestDto(1, null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> cartItemService.addItemToCart(request));

        assertEquals("Quantity cannot be null", exception.getMessage(), "Quantity cannot be null");
    }

    @Test
    void addItemToCartThrowsIfQuantityLessThanOne() {
        CartItemRequestDto request = new CartItemRequestDto(1, 0);

        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> cartItemService.addItemToCart(request));

//        assertEquals("Quantity must be at least 1", exception.getMessage());
        assertEquals("Quantity must be at least 1", exception.getMessage(), "Quantity must be at least 1");
    }

    @Test
    void addItemToCartThrowsIfProductNotFound() {
        CartItemRequestDto request = new CartItemRequestDto(999, 2);

        when(productService.getProductById(999)).thenReturn(Optional.empty());

        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> cartItemService.addItemToCart(request));

        assertEquals("Product with ID: 999 not found", exception.getMessage(), "Product with ID: 999 not found");
    }
}