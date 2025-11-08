package org.onlineshop.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.onlineshop.dto.favourite.FavouriteResponseDto;
import org.onlineshop.entity.Favourite;
import org.onlineshop.entity.Product;
import org.onlineshop.entity.User;
import org.onlineshop.exception.NotFoundException;
import org.onlineshop.repository.FavouriteRepository;
import org.onlineshop.repository.ProductRepository;
import org.onlineshop.service.converter.FavouriteConverter;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class FavouriteServiceDeleteFromFavouriteTest {
    @Mock
    private FavouriteRepository favouriteRepository;

    @Mock
    private UserService userService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private FavouriteConverter favouriteConverter;

    private FavouriteService favouriteService;

    private User user;
    private Product product;
    private Favourite favourite;
    private FavouriteResponseDto favouriteResponseDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        favouriteService = new FavouriteService(favouriteRepository, userService, productRepository, favouriteConverter);

        user = new User();
        user.setUserId(1);

        product = new Product();
        product.setId(10);

        favourite = new Favourite();
        favourite.setFavouriteId(100);
        favourite.setUser(user);
        favourite.setProduct(product);

        favouriteResponseDto = new FavouriteResponseDto(100, "testProduct");
    }

    @Test
    void deleteFavourite() {
        when(userService.getCurrentUser()).thenReturn(user);
        when(favouriteRepository.findByUser(user)).thenReturn(List.of(favourite));
        when(favouriteConverter.toDto(favourite)).thenReturn(favouriteResponseDto);

        FavouriteResponseDto result = favouriteService.deleteFavourite(10);

        assertEquals(favouriteResponseDto, result);
    }

    @Test
    void deleteFavouriteWhenProductIsNull() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> favouriteService.deleteFavourite(null));
        assertEquals("Product Id cannot be null", exception.getMessage());
        verifyNoInteractions(userService, productRepository, favouriteRepository, favouriteConverter);
    }

    @Test
    void deleteFavouriteIfProductNotFound() {
        when(userService.getCurrentUser()).thenReturn(user);
        when(favouriteRepository.findByUser(user)).thenReturn(List.of());

        assertEquals("Product not found in favourites", assertThrows(NotFoundException.class, () -> favouriteService.deleteFavourite(10)).getMessage());
    }
}