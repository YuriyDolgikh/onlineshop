package org.onlineshop.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.onlineshop.dto.cart.CartResponseDto;
import org.onlineshop.dto.cartItem.CartItemSympleResponseDto;
import org.onlineshop.entity.User;
import org.onlineshop.repository.UserRepository;
import org.onlineshop.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.yml")
class CartControllerGetFullDataTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CartService cartService;

    private CartResponseDto responseDto;

    @Autowired
    private UserRepository userRepository;

    private User userTest;

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @BeforeEach
    void setUp() {
        User newTestUser = User.builder()
                .username("newTestUser")
                .email("testUser@email.com")
                .hashPassword("$2a$10$WiAt7dmC1vLIxjY9/9n7P.I5RQU1MKKSOI1Dy1pNLPPIts7K5RJR2")
                .phoneNumber("+494949494949")
                .status(User.Status.CONFIRMED)
                .role(User.Role.USER)
                .build();

        userTest = userRepository.save(newTestUser);

        responseDto = CartResponseDto.builder()
                .userId(userTest.getUserId())
                .totalPrice(BigDecimal.valueOf(200))
                .cartSympleItems(List.of(
                        CartItemSympleResponseDto.builder()
                                .productName("TestProduct")
                                .quantity(2)
                                .build()
                ))
                .build();
    }

    @Test
    @WithMockUser(username = "testUser@email.com",
            roles = {"ADMIN", "MANAGER", "USER"})
    void testGetCartFullDataIfOkAndRoleAdminManagerUser() throws Exception {

        when(cartService.getCartFullData()).thenReturn(responseDto);

        mockMvc.perform(get("/v1/carts"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.userId").value(userTest.getUserId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.totalPrice").value(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$.cartSympleItems[0].productName").value("TestProduct"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.cartSympleItems[0].quantity").value(2));

        verify(cartService, times(1)).getCartFullData();
    }

    @Test
    void testGetCartFullDataIfUserNotRegistered() throws Exception {
        mockMvc.perform(get("/v1/carts"))
                .andExpect(status().isUnauthorized());
    }
}