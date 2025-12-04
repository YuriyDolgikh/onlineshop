package org.onlineshop.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.onlineshop.dto.cartItem.CartItemRequestDto;
import org.onlineshop.dto.cartItem.CartItemSimpleResponseDto;
import org.onlineshop.entity.Cart;
import org.onlineshop.entity.CartItem;
import org.onlineshop.entity.Product;
import org.onlineshop.entity.User;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.AssertionErrors.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.yml")
class CartItemServiceGetCartItemFromCartTest {

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

    @AfterEach
    void tearDown() {
        cartItemRepository.deleteAll();
        productRepository.deleteAll();
        userRepository.deleteAll();
        cartRepository.deleteAll();
    }

    @Test
    void getCartItemFromCartIfOk() {
        Product product = new Product();
        product.setId(1);

        CartItem existingItem = CartItem.builder()
                .product(product)
                .quantity(2)
                .build();

        Cart cart = new Cart();
        cart.setCartItems(Set.of(existingItem));

        User user = new User();
        user.setCart(cart);

        when(userService.getCurrentUser()).thenReturn(user);
        when(productService.getProductById(1)).thenReturn(Optional.of(product));
        when(cartItemRepository.save(any(CartItem.class))).thenAnswer(i -> i.getArgument(0));
        when(cartItemConverter.toSimpleDto(any(CartItem.class)))
                .thenAnswer(i -> {
                    CartItem item = i.getArgument(0);
                    CartItemSimpleResponseDto dto = new CartItemSimpleResponseDto();
                    dto.setQuantity(item.getQuantity());
                    return dto;
                });

        CartItemRequestDto request = new CartItemRequestDto(1, 3);
        CartItemSimpleResponseDto response = cartItemService.addItemToCart(request);

        // Проверяем, что количество увеличилось на 3
        assertEquals("Quantity should be 5", 5, response.getQuantity());

        verify(userService, times(2)).getCurrentUser();
        verify(productService, times(1)).getProductById(1);
        verify(cartItemRepository, times(1)).save(existingItem);
        verify(cartItemConverter, times(1)).toSimpleDto(existingItem);
    }

    @Test
    void getCartItemFromCartIfProductNotIcCartThenCreatesNewCartItem() {
        Product product = new Product();
        product.setId(3);

        Cart cart = new Cart();
        cart.setCartItems(new HashSet<>());

        User user = new User();
        user.setCart(cart);

        when(userService.getCurrentUser()).thenReturn(user);
        when(productService.getProductById(3)).thenReturn(Optional.of(product));
        when(cartItemRepository.save(any(CartItem.class))).thenAnswer(i -> i.getArgument(0));
        when(cartItemConverter.toSimpleDto(any(CartItem.class))).thenAnswer(i -> {
            CartItem item = i.getArgument(0);
            CartItemSimpleResponseDto dto = new CartItemSimpleResponseDto();
            dto.setQuantity(item.getQuantity());
            return dto;
        });

        CartItemRequestDto request = new CartItemRequestDto(3, 4);
        CartItemSimpleResponseDto response = cartItemService.addItemToCart(request);

        assertEquals("Quantity should be 4", 4, response.getQuantity());
        assertEquals("Cart should contain 1 item", 1, user.getCart().getCartItems().size());

        CartItem addedItem = user.getCart().getCartItems().iterator().next();
        assertEquals("Product id should match", 3, addedItem.getProduct().getId());
        assertEquals("Quantity should match request", 4, addedItem.getQuantity());

        verify(userService, times(2)).getCurrentUser();
        verify(productService, times(2)).getProductById(3);
        verify(cartItemRepository, times(1)).save(addedItem);
        verify(cartItemConverter, times(1)).toSimpleDto(addedItem);
    }
}