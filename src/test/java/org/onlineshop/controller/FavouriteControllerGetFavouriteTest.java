package org.onlineshop.controller;

import org.junit.jupiter.api.Test;
import org.onlineshop.dto.favourite.FavouriteResponseDto;
import org.onlineshop.service.FavouriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.yml")
class FavouriteControllerGetFavouriteTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FavouriteService favouriteService;

    @Test
    void getFavouriteIfOk() throws Exception {

        List<FavouriteResponseDto> list = List.of(
                new FavouriteResponseDto(1,"aaaa"),
                new FavouriteResponseDto(5,"bbbb"),
                new FavouriteResponseDto(13,"dddd")
        );

        when(favouriteService.getFavourites()).thenReturn(list);

        mockMvc.perform(get("v1/favorites"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.size()").value(3))

                .andExpect(jsonPath("$[0].favouriteId").value(1))
                .andExpect(jsonPath("$[0].productName").value("aaaa"))
                .andExpect(jsonPath("$[1].favouriteId").value(5))
                .andExpect(jsonPath("$[1].productName").value("bbbb"))
                .andExpect(jsonPath("$[2].favouriteId").value(13))
                .andExpect(jsonPath("$[2].productName").value("dddd"));

        verify(favouriteService, times(1)).getFavourites();
    }

    @Test
    void getFavouriteIfEmpty() throws Exception {

        when(favouriteService.getFavourites()).thenReturn(List.of());

        mockMvc.perform(get("v1/favorites"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(0));

        verify(favouriteService, times(1)).getFavourites();
    }

    @Test
    void getFavouriteIfErrors() throws Exception {
        when(favouriteService.getFavourites())
                .thenThrow(new RuntimeException("Test service failure"));

        mockMvc.perform(get("/v1/favorites"))
                .andExpect(status().is5xxServerError());
    }
}