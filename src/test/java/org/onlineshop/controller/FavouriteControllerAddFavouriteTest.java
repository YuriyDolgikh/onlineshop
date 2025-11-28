package org.onlineshop.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.onlineshop.dto.favourite.FavouriteResponseDto;
import org.onlineshop.exception.BadRequestException;
import org.onlineshop.exception.NotFoundException;
import org.onlineshop.service.FavouriteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FavouriteControllerAddFavouriteTest {

    @Mock
    private FavouriteService favouriteService;

    @InjectMocks
    private FavouriteController favouriteController;

    @Test
    void addFavouriteIfOk() {
        FavouriteResponseDto dto = new FavouriteResponseDto(5, "bbbb");
        when(favouriteService.addFavourite(5)).thenReturn(dto);

        ResponseEntity<FavouriteResponseDto> response = favouriteController.addFavourite(5);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(5, response.getBody().getFavouriteId());
        assertEquals("bbbb", response.getBody().getProductName());
        verify(favouriteService, times(1)).addFavourite(5);
    }

    @Test
    void addFavouriteIfProductIdNull() {
        when(favouriteService.addFavourite(null))
                .thenThrow(new IllegalArgumentException("Product Id cannot be null"));

        assertThrows(IllegalArgumentException.class, () -> favouriteController.addFavourite(null));
        verify(favouriteService, times(1)).addFavourite(null);
    }

    @Test
    void addFavouriteIfProductNotFound() {
        when(favouriteService.addFavourite(99999))
                .thenThrow(new NotFoundException("Product not found with ID: 999"));

        assertThrows(NotFoundException.class, () -> favouriteController.addFavourite(99999));
        verify(favouriteService, times(1)).addFavourite(99999);
    }

    @Test
    void addFavouriteIAlreadyExist() {
        Integer productId = 2;
        doThrow(new BadRequestException("Product is already in favourites"))
                .when(favouriteService).addFavourite(productId);

        assertThrows(BadRequestException.class, () -> favouriteController.addFavourite(productId));
        verify(favouriteService, times(1)).addFavourite(productId);
    }

    @Test
    void addFavouriteIfServiceThrows() {
        when(favouriteService.addFavourite(10)).thenThrow(new RuntimeException("Something bad"));

        assertThrows(RuntimeException.class, () -> favouriteController.addFavourite(10));
    }
}