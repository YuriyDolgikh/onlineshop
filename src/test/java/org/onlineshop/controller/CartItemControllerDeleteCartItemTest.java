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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.yml")
class CartItemControllerDeleteCartItemTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CartItemService cartItemService;

    @Test
    @WithMockUser(
            username = "testUser@email.com",
            roles = {"ADMIN", "MANAGER", "USER"}
    )
    void deleteCartItemIfOk() throws Exception {
        int productId = 10;

        CartItemResponseDto responseDto = CartItemResponseDto.builder()
                .product(Product.builder()
                        .id(productId)
                        .name("Test Product")
                        .price(BigDecimal.valueOf(199))
                        .build())
                .quantity(3)
                .build();

        when(cartItemService.removeItemFromCart(productId))
                .thenReturn(responseDto);

        mockMvc.perform(delete("/v1/cartItems/{productId}", productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.product.name").value("Test Product"))
                .andExpect(jsonPath("$.quantity").value(3));
    }

    @Test
    @WithMockUser(
            username = "testUser@email.com",
            roles = {"ADMIN", "MANAGER", "USER"}
    )
    void deleteCartItemIfNotFound() throws Exception {
        int productId = 999;

        when(cartItemService.removeItemFromCart(productId))
                .thenThrow(new NotFoundException("Product not found"));

        mockMvc.perform(delete("/v1/cartItems/{productId}", productId))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteCartItemIfUnauthorized() throws Exception {
        mockMvc.perform(delete("/v1/cartItems/5"))
                .andExpect(status().isUnauthorized());
    }
}
