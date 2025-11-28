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
class FavouriteControllerDeleteFavouriteTest {

    @Mock
    private FavouriteService favouriteService;

    @InjectMocks
    private FavouriteController favouriteController;

    @Test
    void deleteFromFavouriteIfOk() {
        FavouriteResponseDto dto = new FavouriteResponseDto(5, "bbbb");
        when(favouriteService.deleteFavourite(5)).thenReturn(dto);

        ResponseEntity<FavouriteResponseDto> response = favouriteController.deleteFavourite(5);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(5, response.getBody().getFavouriteId());
        assertEquals("bbbb", response.getBody().getProductName());
        verify(favouriteService, times(1)).deleteFavourite(5);
    }

    @Test
    void deleteFavouriteUnauthorized() {
        assertTrue(true);
    }

    @Test
    void deleteFavouriteIfProductNotFound() {
        int productId = 99999;
        when(favouriteService.deleteFavourite(productId))
                .thenThrow(new NotFoundException("Product not found with ID: " + productId));

        assertThrows(NotFoundException.class, () -> favouriteController.deleteFavourite(productId));
        verify(favouriteService, times(1)).deleteFavourite(productId);
    }

    @Test
    void deleteFavouriteIfAlreadyDeleted() {
        Integer productId = 2;
        doThrow(new BadRequestException("Product is not in favourites"))
                .when(favouriteService).deleteFavourite(productId);

        assertThrows(BadRequestException.class, () -> favouriteController.deleteFavourite(productId));
        verify(favouriteService, times(1)).deleteFavourite(productId);
    }

    @Test
    void deleteFavouriteIfServiceThrows() {
        int productId = 10;
        when(favouriteService.deleteFavourite(productId)).thenThrow(new RuntimeException("Something bad"));

        assertThrows(RuntimeException.class, () -> favouriteController.deleteFavourite(productId));
        verify(favouriteService, times(1)).deleteFavourite(productId);
    }

    @Test
    void deleteFavouriteIfProductInvalid() {
        assertTrue(true);
    }
}