package org.onlineshop.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.onlineshop.dto.favourite.FavouriteResponseDto;
import org.onlineshop.service.FavouriteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FavouriteControllerGetFavouriteTest {

    @Mock
    private FavouriteService favouriteService;

    @InjectMocks
    private FavouriteController favouriteController;

    @Test
    void getFavouriteIfOk() {
        List<FavouriteResponseDto> list = List.of(
                new FavouriteResponseDto(1, "aaaa"),
                new FavouriteResponseDto(5, "bbbb"),
                new FavouriteResponseDto(13, "dddd")
        );

        when(favouriteService.getFavourites()).thenReturn(list);

        ResponseEntity<List<FavouriteResponseDto>> response = favouriteController.getFavorites();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(3, response.getBody().size());
        assertEquals(1, response.getBody().get(0).getFavouriteId());
        assertEquals("aaaa", response.getBody().get(0).getProductName());
        assertEquals(5, response.getBody().get(1).getFavouriteId());
        assertEquals("bbbb", response.getBody().get(1).getProductName());
        assertEquals(13, response.getBody().get(2).getFavouriteId());
        assertEquals("dddd", response.getBody().get(2).getProductName());
        verify(favouriteService, times(1)).getFavourites();
    }

    @Test
    void getFavouriteIfEmpty() {
        when(favouriteService.getFavourites()).thenReturn(List.of());

        ResponseEntity<List<FavouriteResponseDto>> response = favouriteController.getFavorites();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
        verify(favouriteService, times(1)).getFavourites();
    }

    @Test
    void getFavouriteIfErrors() {
        when(favouriteService.getFavourites())
                .thenThrow(new RuntimeException("Test service failure"));

        assertThrows(RuntimeException.class, () -> favouriteController.getFavorites());
    }
}