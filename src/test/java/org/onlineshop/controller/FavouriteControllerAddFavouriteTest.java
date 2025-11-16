package org.onlineshop.controller;

import org.onlineshop.exception.BadRequestException;
import org.junit.jupiter.api.Test;
import org.onlineshop.dto.favourite.FavouriteResponseDto;
import org.onlineshop.exception.NotFoundException;
import org.onlineshop.service.FavouriteService;
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

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.yml")
class FavouriteControllerAddFavouriteTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FavouriteService favouriteService;

    @Test
    @WithMockUser(username = "testUser@email.com",
            roles = {"ADMIN", "MANAGER", "USER"})
    void addFavouriteIfOk() throws Exception {

        FavouriteResponseDto dto = new FavouriteResponseDto(5, "bbbb");

        when(favouriteService.addFavourite(5)).thenReturn(dto);

        mockMvc.perform(post("/v1/favorites/5"))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                .andExpect(jsonPath("$.favouriteId").value(5))
                .andExpect(jsonPath("$.productName").value("bbbb"));

        verify(favouriteService, times(1)).addFavourite(5);
    }

    @Test
    void addFavouriteUnauthorized() throws Exception {
        mockMvc.perform(post("/v1/favorites/5"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "testUser@email.com",
            roles = {"ADMIN", "MANAGER", "USER"})
    void addFavouriteIfProductIdNull() throws Exception {
        when(favouriteService.addFavourite(null))
                .thenThrow(new IllegalArgumentException("Product Id cannot be null"));

        mockMvc.perform(post("/v1/favorites/null"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "testUser@email.com",
            roles = {"ADMIN", "MANAGER", "USER"})
    void addFavouriteIfProductNotFound() throws Exception {

        when(favouriteService.addFavourite(99999))
                .thenThrow(new NotFoundException("Product not found with ID: 999"));

        mockMvc.perform(post("/v1/favorites/99999"))
                .andExpect(status().isNotFound());
        verify(favouriteService, times(1)).addFavourite(any());
    }

    @Test
    @WithMockUser(username = "testUser@email.com",
            roles = {"ADMIN", "MANAGER", "USER"})
    void addFavouriteIAlreadyExist() throws Exception {
        Integer productId = 2;

        doThrow(new BadRequestException("Product is already in favourites")).when(favouriteService).addFavourite(productId);

        mockMvc.perform(post("/v1/favorites/{productId}",productId))
                .andExpect(status().isBadRequest());
        verify(favouriteService, times(1)).addFavourite(productId);
    }

    @Test
    @WithMockUser(username = "testUser@email.com",
            roles = {"ADMIN", "MANAGER", "USER"})
    void addFavouriteIfServiceThrows() throws Exception {
        when(favouriteService.addFavourite(10)).thenThrow(new RuntimeException("Something bad"));

        mockMvc.perform(post("/v1/favorites/10"))
                .andExpect(status().is5xxServerError());
    }

    @Test
    @WithMockUser(username = "testUser@email.com",
            roles = {"ADMIN", "MANAGER", "USER"})
    void addFavouriteIfProsuctInvalid() throws Exception {
        mockMvc.perform(post("/v1/favorites/abc")).andExpect(status().isBadRequest());
    }
}