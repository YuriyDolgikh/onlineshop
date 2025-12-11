package org.onlineshop.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.onlineshop.dto.favourite.FavouriteResponseDto;
import org.onlineshop.service.FavouriteService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
        Page<FavouriteResponseDto> page = new PageImpl<>(list);

        Pageable pageable = PageRequest.of(0, 10);
        when(favouriteService.getFavourites(pageable)).thenReturn(page);

        ResponseEntity<Page<FavouriteResponseDto>> response = favouriteController.getFavorites(0, 10);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(3, response.getBody().getTotalElements());
        assertEquals(1, response.getBody().getContent().get(0).getFavouriteId());
        assertEquals("aaaa", response.getBody().getContent().get(0).getProductName());
        assertEquals(5, response.getBody().getContent().get(1).getFavouriteId());
        assertEquals("bbbb", response.getBody().getContent().get(1).getProductName());
        assertEquals(13, response.getBody().getContent().get(2).getFavouriteId());
        assertEquals("dddd", response.getBody().getContent().get(2).getProductName());
        verify(favouriteService, times(1)).getFavourites(pageable);
    }

    @Test
    void getFavouriteIfEmpty() {
        Page<FavouriteResponseDto> emptyPage = new PageImpl<>(List.of());
        Pageable pageable = PageRequest.of(0, 10);
        when(favouriteService.getFavourites(pageable)).thenReturn(emptyPage);

        ResponseEntity<Page<FavouriteResponseDto>> response = favouriteController.getFavorites(0, 10);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
        assertEquals(0, response.getBody().getTotalElements());
        verify(favouriteService, times(1)).getFavourites(pageable);
    }

    @Test
    void getFavouriteIfErrors() {
        Pageable pageable = PageRequest.of(0, 10);
        when(favouriteService.getFavourites(pageable))
                .thenThrow(new RuntimeException("Test service failure"));

        assertThrows(RuntimeException.class, () -> favouriteController.getFavorites(0, 10));
    }
}