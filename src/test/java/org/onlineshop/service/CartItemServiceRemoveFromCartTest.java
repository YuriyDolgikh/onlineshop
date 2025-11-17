package org.onlineshop.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.onlineshop.dto.cartItem.CartItemResponseDto;
import org.onlineshop.entity.*;
import org.onlineshop.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.yml")
class CartItemServiceRemoveFromCartTest {

    @Autowired
    private CartItemService cartItemService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private CartItemRepository cartItemRepository;

    private User testUser;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        cartItemRepository.deleteAll();
        cartRepository.deleteAll();
        productRepository.deleteAll();
        userRepository.deleteAll();
        categoryRepository.deleteAll();

       long stamp = Math.abs(System.currentTimeMillis());

        testUser = User.builder()
                .username("testUser" + (stamp% 1_00))
                .email("testUser" + stamp + "@tmail.com")
                .hashPassword(stamp + "")
                .phoneNumber("+491234567" + (stamp % 1_00000))
                .role(User.Role.USER)
                .status(User.Status.CONFIRMED)
                .favourites(new HashSet<>())
                .orders(new ArrayList<>())
                .build();
        userRepository.save(testUser);

        System.out.println(testUser.getPhoneNumber() + " ==============================");

        Cart cart = new Cart();
        cart.setUser(testUser);
        cartRepository.save(cart);
        testUser.setCart(cart);
        userRepository.save(testUser);

        Category category = Category.builder()
                .categoryName("Category" + stamp)
                .build();
        categoryRepository.save(category);

        testProduct = Product.builder()
                .name("ProductA")
                .price(BigDecimal.valueOf(200))
                .category(category)
                .build();
        productRepository.save(testProduct);


        CartItem item = CartItem.builder()
                .cart(cart)
                .product(testProduct)
                .quantity(3)
                .build();
        cartItemRepository.save(item);

        cart.getCartItems().add(item);
        cartRepository.save(cart);
    }

    @Test
    @WithMockUser(username = "#{testUser.email}", roles = {"USER"})
    void testRemoveItemFromCartIfOk() {
        CartItemResponseDto removed = cartItemService.removeItemFromCart(testProduct.getId());

        assertNotNull(removed);
        assertEquals(testProduct.getId(), removed.getProduct().getId());
        assertEquals(3, removed.getQuantity());

        Cart cartAfter = cartRepository.findByUser(testUser).orElseThrow();
        assertTrue(cartAfter.getCartItems().isEmpty(),"Cart should be empty after removal");
        assertEquals(0,cartItemRepository.count(),"Cart should be empty after removal");

    }


}