package org.onlineshop.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.onlineshop.dto.cartItem.CartItemFullResponseDto;
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

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import static org.mockito.Mockito.*;
import static org.springframework.test.util.AssertionErrors.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.yml")
class CartItemServiceGetCartItemsTest {

    @Autowired
    private CartItemService cartItemService;

    @MockBean
    private UserService userService;

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

    @BeforeEach
    void setUp() {
        testUser = new User();
        testCart = new Cart();
        testUser.setCart(testCart);

        Product p1 = new Product();
        p1.setId(1);
        p1.setName("Product1");
        p1.setPrice(new BigDecimal("100"));

        Product p2 = new Product();
        p2.setId(2);
        p2.setName("Product2");
        p2.setPrice(new BigDecimal("200"));

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

        when(userService.getCurrentUser()).thenReturn(testUser);
    }

    @AfterEach
    void tearDown() {
        productRepository.deleteAll();
        userRepository.deleteAll();
        cartRepository.deleteAll();
    }

    @Test
    void getCartItemsReturnsFullDtoSet() {
        when(cartService.getCurrentCart()).thenReturn(testCart);

        when(cartItemConverter.toFullDtos(any(Set.class))).thenAnswer(invocation -> {
            Set<CartItem> items = invocation.getArgument(0);
            Set<CartItemFullResponseDto> dtos = new HashSet<>();
            for (CartItem ci : items) {
                CartItemFullResponseDto dto = new CartItemFullResponseDto();
                dto.setQuantity(ci.getQuantity());
                dto.setProductName(ci.getProduct().getName());
                dtos.add(dto);
            }
            return dtos;
        });

        Set<CartItemFullResponseDto> response = cartItemService.getCartItems();

        assertEquals("Number of items should match", 2, response.size());

        boolean product1Found = response.stream().anyMatch(dto -> "Product1".equals(dto.getProductName()) && dto.getQuantity() == 1);
        boolean product2Found = response.stream().anyMatch(dto -> "Product2".equals(dto.getProductName()) && dto.getQuantity() == 2);

        assertEquals("Product1 should be present", true, product1Found);
        assertEquals("Product2 should be present", true, product2Found);

        verify(userService, times(0)).getCurrentUser();
        verify(cartService, times(1)).getCurrentCart();
        verify(cartItemConverter, times(1)).toFullDtos(testCart.getCartItems());
    }
}