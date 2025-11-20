package org.onlineshop.controller;

import org.junit.jupiter.api.Test;
import org.onlineshop.dto.favourite.FavouriteResponseDto;
import org.onlineshop.exception.BadRequestException;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.yml")
class FavouriteControllerDeleteFavouriteTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FavouriteService favouriteService;

    @Test
    @WithMockUser(username = "testUser@email.com",
            roles = {"ADMIN", "MANAGER", "USER"})
    void deleteFromFavouriteIfOk() throws Exception {
        FavouriteResponseDto dto = new FavouriteResponseDto(5, "bbbb");

        when(favouriteService.deleteFavourite(5)).thenReturn(dto);

        mockMvc.perform(delete("/v1/favorites/5"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                .andExpect(jsonPath("$.favouriteId").value(5))
                .andExpect(jsonPath("$.productName").value("bbbb"));

        verify(favouriteService, times(1)).deleteFavourite(5);
    }

    @Test
    void deleteFavouriteUnauthorized() throws Exception {
        mockMvc.perform(delete("/v1/favorites/5"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "testUser@email.com",
            roles = {"ADMIN", "MANAGER", "USER"})
    void deleteFavouriteIfProductNotFound() throws Exception {
        int productId = 99999;
        when(favouriteService.deleteFavourite(productId))
                .thenThrow(new NotFoundException("Product not found with ID: " + productId));

        mockMvc.perform(delete("/v1/favorites/{productId}", productId))
                .andExpect(status().isNotFound());
        verify(favouriteService, times(1)).deleteFavourite(productId);
    }

    @Test
    @WithMockUser(username = "testUser@email.com",
            roles = {"ADMIN", "MANAGER", "USER"})
    void deleteFavouriteIfAlreadyDeleted() throws Exception {
        Integer productId = 2;

        doThrow(new BadRequestException("Product is not in favourites"))
                .when(favouriteService).deleteFavourite(productId);

        mockMvc.perform(delete("/v1/favorites/{productId}", productId))
                .andExpect(status().isBadRequest());

        verify(favouriteService, times(1)).deleteFavourite(productId);
    }

    @Test
    @WithMockUser(username = "testUser@email.com",
            roles = {"ADMIN", "MANAGER", "USER"})
    void deleteFavouriteIfServiceThrows() throws Exception {
        int productId = 10;
        when(favouriteService.deleteFavourite(productId)).thenThrow(new RuntimeException("Something bad"));

        mockMvc.perform(delete("/v1/favorites/{productId}", productId))
                .andExpect(status().is5xxServerError());
        verify(favouriteService, times(1)).deleteFavourite(productId);
    }

    @Test
    @WithMockUser(username = "testUser@email.com",
            roles = {"ADMIN", "MANAGER", "USER"})
    void deleteFavouriteIfProductInvalid() throws Exception {
        mockMvc.perform(delete("/v1/favorites/abc")).andExpect(status().isBadRequest());
        verify(favouriteService, times(0)).deleteFavourite(any());
    }
}