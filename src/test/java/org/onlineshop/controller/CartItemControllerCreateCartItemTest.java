package org.onlineshop.controller;

import org.junit.jupiter.api.Test;
import org.onlineshop.dto.cartItem.CartItemRequestDto;
import org.onlineshop.dto.cartItem.CartItemSympleResponseDto;
import org.onlineshop.exception.NotFoundException;
import org.onlineshop.service.CartItemService;
import org.onlineshop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.yml")
class CartItemControllerCreateCartItemTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CartItemService cartItemService;

    @MockBean
    private UserService userService;

    @Test
    @WithMockUser(username = "testUser@email.com",
            roles = {"ADMIN", "MANAGER","USER"})
            void createCartItemTestIfOk() throws Exception{
        CartItemRequestDto request = new CartItemRequestDto(10,3);
        CartItemSympleResponseDto response = CartItemSympleResponseDto.builder()
                .productName("product")
                .quantity(request.getQuantity())
                .build();

        when(cartItemService.addItemToCart(any())).thenReturn(response);

                mockMvc.perform(
                        post("/v1/cartItems")
                                .contentType("application/json")
                                .content("""
                                        {
                                         "productId": 10,
                                         "quantity": 3
                                          }""")
                )
                        .andExpect(status().isCreated())
                        .andExpect(jsonPath("$.productName").value("product"))
                        .andExpect(jsonPath("$.quantity").value(3));
    }

    @Test
    void createCartItemUnauthorized() throws Exception {
        mockMvc.perform(
                        post("/v1/cartItems")
                                .contentType("application/json")
                                .content("{\"productId\": 10, \"quantity\": 3}")
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void createCartItemWhenProductIdMissing() throws Exception {
        mockMvc.perform(
                        post("/v1/cartItems")
                                .contentType("application/json")
                                .content("""
                            {"quantity": 3}
                            """)
                )
                .andExpect(status().isBadRequest());
    }
    @Test
    @WithMockUser
    void createCartItemWhenQuantityMissing() throws Exception {
        mockMvc.perform(
                        post("/v1/cartItems")
                                .contentType("application/json")
                                .content("""
                            {"productId": 10}
                            """)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "testUser@email.com",
            roles = {"ADMIN", "MANAGER","USER"})
    void createCartItemWhenProductNotFound() throws Exception {
        when(cartItemService.addItemToCart(any()))
                .thenThrow(new NotFoundException("Product not found"));
        mockMvc.perform(post("/v1/cartItems")
                        .contentType("application/json")
                        .content("""
                        {"productId": 10, "quantity": 1}
                    """))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "testUser@email.com",
            roles = {"ADMIN", "MANAGER","USER"})
    void createCartItemWhenQuantityInvalid() throws Exception {
        mockMvc.perform(
                        post("/v1/cartItems")
                                .contentType("application/json")
                                .content("""
                            {"productId": 10, "quantity": 0}
                            """)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "testUser@email.com",
            roles = {"ADMIN", "MANAGER","USER"})
    void createCartItemWhenServiceThrowsException() throws Exception {
        when(cartItemService.addItemToCart(any()))
                .thenThrow(new RuntimeException("Boom"));

        mockMvc.perform(post("/v1/cartItems")
                        .contentType("application/json")
                        .content("""
                        {"productId": 10, "quantity": 2}
                    """))
                .andExpect(status().isInternalServerError());
    }
}