package org.onlineshop.controller;

import org.junit.jupiter.api.Test;
import org.onlineshop.dto.cartItem.CartItemFullResponseDto;

import org.onlineshop.entity.Cart;
import org.onlineshop.service.CartItemService;
import org.onlineshop.service.CartService;
import org.onlineshop.service.converter.CartItemConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.yml")
class CartItemControllerGetCartItemsTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private CartItemService cartItemService;
    @MockBean
    private CartService cartService;

    @Test
    @WithMockUser(username = "testUser@email.com",
            roles = {"ADMIN", "MANAGER", "USER"})
    void getCartItemsIfOk() throws Exception {
        Set<CartItemFullResponseDto> set = new LinkedHashSet<>();
        for (int i = 0; i < 5; i++) {
            set.add(CartItemFullResponseDto.builder()
                    .cartItemId(1 + i)
                    .productName("Product " + i)
                    .categoryName("Category")
                    .productPrice(100.0 + (i + 20))
                    .productDiscountPrice(15.0 - i)
                    .quantity(2 + i)
                    .build());
        }
        when(cartItemService.getCartItems()).thenReturn(set);

        mockMvc.perform(get("/v1/cartItems"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(set.size()))
                .andExpect(jsonPath("$[0].productName").value("Product 0"));
    }
    @Test
    @WithMockUser(username = "testUser@email.com",
            roles = {"ADMIN", "MANAGER", "USER"})
    void getCartItemsIfCartEmpty() throws Exception {
        Cart emptyCart = new Cart();
        emptyCart.setCartItems(new HashSet<>());

        when(cartService.getCurrentCart()).thenReturn(emptyCart);

        mockMvc.perform(get("/v1/cartItems"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }


    @Test
    void getCartItemsIfUnauthorized() throws Exception {
        mockMvc.perform(get("/v1/cartItems"))
                .andExpect(status().isUnauthorized());
    }

}