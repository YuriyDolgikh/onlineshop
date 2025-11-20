package org.onlineshop.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.onlineshop.dto.cartItem.CartItemResponseDto;
import org.onlineshop.dto.cartItem.CartItemUpdateDto;
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

import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.yml")
class CartItemControllerUpdateCartItemTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CartItemService cartItemService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "testUser@email.com",
            roles = {"ADMIN", "MANAGER","USER"})
    void updateCartItemTestIfOk() throws Exception {
        CartItemUpdateDto updateDto = new CartItemUpdateDto();
        updateDto.setProductId(1);
        updateDto.setQuantity(5);

        Product product = new Product();
        product.setId(1);
        product.setName("Test Product");
        product.setPrice(new BigDecimal("100"));

        CartItemResponseDto responseDto = new CartItemResponseDto();
        responseDto.setProduct(product);
        responseDto.setQuantity(5);

        when(cartItemService.updateItemInCart(any(CartItemUpdateDto.class))).thenReturn(responseDto);

        mockMvc.perform(
                        put("/v1/cartItems")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateDto))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.product.name").value("Test Product"))
                .andExpect(jsonPath("$.quantity").value(5));
    }


    @Test
    void updateCartItemUnauthorized() throws Exception {
        CartItemUpdateDto updateDto = new CartItemUpdateDto();
        updateDto.setProductId(1);
        updateDto.setQuantity(5);

        mockMvc.perform(put("/v1/cartItems")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isUnauthorized());
    }
    @Test
    @WithMockUser(username = "testUser@email.com",
            roles = {"ADMIN", "MANAGER","USER"})
    void updateCartItemInvalidQuantity() throws Exception {
        CartItemUpdateDto updateDto = new CartItemUpdateDto();
        updateDto.setProductId(1);
        updateDto.setQuantity(0);

        doThrow(new IllegalArgumentException("Quantity must be at least 1"))
                .when(cartItemService).updateItemInCart(any(CartItemUpdateDto.class));

        mockMvc.perform(put("/v1/cartItems")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "testUser@email.com",
            roles = {"ADMIN", "MANAGER","USER"})
    void updateCartItemProductNotFound() throws Exception {
        CartItemUpdateDto updateDto = new CartItemUpdateDto();
        updateDto.setProductId(999);
        updateDto.setQuantity(3);

        doThrow(new NotFoundException("Product not found"))
                .when(cartItemService).updateItemInCart(any(CartItemUpdateDto.class));

        mockMvc.perform(put("/v1/cartItems")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "testUser@email.com",
            roles = {"ADMIN", "MANAGER","USER"})
    void updateCartItemMissingQuantity() throws Exception {
        CartItemUpdateDto updateDto = new CartItemUpdateDto();
        updateDto.setProductId(1);

        mockMvc.perform(put("/v1/cartItems")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "testUser@email.com",
            roles = {"ADMIN", "MANAGER","USER"})
    void updateCartItemServiceThrowsException() throws Exception {
        CartItemUpdateDto updateDto = new CartItemUpdateDto();
        updateDto.setProductId(1);
        updateDto.setQuantity(2);

        doThrow(new RuntimeException("Unexpected error"))
                .when(cartItemService).updateItemInCart(any(CartItemUpdateDto.class));

        mockMvc.perform(put("/v1/cartItems")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isInternalServerError());
    }
}