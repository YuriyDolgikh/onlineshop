package org.onlineshop.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.onlineshop.dto.cartItem.CartItemUpdateDto;
import org.onlineshop.dto.cartItem.CartItemResponseDto;
import org.onlineshop.entity.Cart;
import org.onlineshop.entity.CartItem;
import org.onlineshop.entity.Product;
import org.onlineshop.entity.User;
import org.onlineshop.exception.BadRequestException;
import org.onlineshop.exception.NotFoundException;
import org.onlineshop.repository.CartItemRepository;
import org.onlineshop.repository.CartRepository;
import org.onlineshop.repository.ProductRepository;
import org.onlineshop.repository.UserRepository;
import org.onlineshop.service.converter.CartItemConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

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
class CartItemServiceUpdateItemInCartTest {

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

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartRepository cartRepository;

    private Cart testCart;
    private User testUser;
    private CartItem existingCartItem;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testCart = new Cart();
        testUser.setCart(testCart);

        Product p1 = new Product();
        p1.setId(1);

        existingCartItem = new CartItem();
        existingCartItem.setProduct(p1);
        existingCartItem.setQuantity(5);

        Set<CartItem> items = new HashSet<>();
        items.add(existingCartItem);
        testCart.setCartItems(items);

        when(userService.getCurrentUser()).thenReturn(testUser);
    }

    @AfterEach
    void tearDown() {
        cartItemRepository.deleteAll();
        productRepository.deleteAll();
        userRepository.deleteAll();
        cartRepository.deleteAll();
    }

    @Test
    void updateItemInCartReturnsCorrectDto() {
        Product product = new Product();
        product.setId(1);
        product.setName("Updated Product");

        when(productService.getProductById(1)).thenReturn(Optional.of(product));
        when(cartItemRepository.save(any(CartItem.class))).thenAnswer(i -> i.getArgument(0));

        when(cartItemConverter.toDto(any(CartItem.class))).thenAnswer(i -> {
            CartItem item = i.getArgument(0);
            CartItemResponseDto dto = new CartItemResponseDto();
            dto.setQuantity(item.getQuantity());
            dto.setProduct(item.getProduct());
            return dto;
        });

        CartItemUpdateDto updateDto = new CartItemUpdateDto(1, 10);

        CartItemResponseDto response = cartItemService.updateItemInCart(updateDto);

        assertEquals("Quantity must update to 10", 10, response.getQuantity());
        assertEquals("Product ID must match", 1, response.getProduct().getId());

        verify(productService, times(1)).getProductById(1);
        verify(cartItemRepository, times(1)).save(existingCartItem);
        verify(cartItemConverter, times(1)).toDto(existingCartItem);
    }

    @Test
    void updateItemInCartThrowsIfProductIdNull() {
        CartItemUpdateDto updateDto = new CartItemUpdateDto(null, 3);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> cartItemService.updateItemInCart(updateDto)
        );

        assertEquals("Product Id cannot be null",
                "Product Id cannot be null", exception.getMessage());
    }

    @Test
    void updateItemInCartThrowsIfQuantityNull() {
        CartItemUpdateDto updateDto = new CartItemUpdateDto(1, null);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> cartItemService.updateItemInCart(updateDto)
        );

        assertEquals("Quantity cannot be null",
                "Quantity cannot be null", exception.getMessage());
    }

    @Test
    void updateItemInCartThrowsIfQuantityLessThanOne() {
        CartItemUpdateDto updateDto = new CartItemUpdateDto(1, 0);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> cartItemService.updateItemInCart(updateDto)
        );

        assertEquals("Quantity must be at least 1",
                "Quantity must be at least 1", exception.getMessage());
    }

    @Test
    void updateItemInCartThrowsIfProductNotFoundInProductService() {
        CartItemUpdateDto updateDto = new CartItemUpdateDto(111, 3);

        when(productService.getProductById(111)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> cartItemService.updateItemInCart(updateDto)
        );

        assertEquals("Product with ID: 111 not found",
                "Product with ID: 111 not found", exception.getMessage());
    }

    @Test
    void updateItemInCartThrowsIfProductNotFoundInUsersCart() {
        Product p = new Product();
        p.setId(999);

        when(productService.getProductById(999)).thenReturn(Optional.of(p));

        CartItemUpdateDto updateDto = new CartItemUpdateDto(999, 10);

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> cartItemService.updateItemInCart(updateDto)
        );

        assertEquals("Product with ID: 999 not found in users cart",
                "Product with ID: 999 not found in users cart", exception.getMessage());
    }
}
