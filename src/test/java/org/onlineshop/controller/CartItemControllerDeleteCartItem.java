package org.onlineshop.controller;

import org.junit.jupiter.api.Test;
import org.onlineshop.dto.cartItem.CartItemResponseDto;
import org.onlineshop.entity.Product;
import org.onlineshop.exception.NotFoundException;
import org.onlineshop.service.CartItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.yml")
public class CartItemControllerDeleteCartItem {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CartItemService cartItemService;

    @Test
    @WithMockUser(username = "testUser@email.com",
            roles = {"ADMIN", "MANAGER","USER"})
    void deleteCartItemIfExists() throws Exception {
        Product product = new Product();
        product.setId(123);
        product.setName("Test Product");
        product.setPrice(new BigDecimal("100.0"));

        CartItemResponseDto response = new CartItemResponseDto();
        response.setProduct(product);
        response.setQuantity(2);

        when(cartItemService.removeItemFromCart(123)).thenReturn(response);

        mockMvc.perform(delete("/v1/cartItems/123")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.product.name").value("Test Product"))
                .andExpect(jsonPath("$.quantity").value(2));
    }

    @Test
    @WithMockUser(username = "testUser@email.com",
            roles = {"ADMIN", "MANAGER","USER"})
    void deleteCartItemIfNotFound() throws Exception {
        doThrow(new NotFoundException("Product not found in cart"))
                .when(cartItemService).removeItemFromCart(999);

        mockMvc.perform(delete("/v1/cartItems/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteCartItemUnauthorized() throws Exception {
        mockMvc.perform(delete("/v1/cartItems/123")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
}
