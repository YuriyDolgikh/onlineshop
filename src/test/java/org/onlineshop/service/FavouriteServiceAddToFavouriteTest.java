package org.onlineshop.service;

import org.junit.jupiter.api.AfterEach;
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
import org.onlineshop.repository.UserRepository;
import org.onlineshop.service.converter.FavouriteConverter;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.yml")
class FavouriteServiceAddToFavouriteTest {
    @Mock
    private FavouriteRepository favouriteRepository;

    @Mock
    private UserService userService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

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

    @AfterEach
    void tearDown() {
        favouriteRepository.deleteAll();
        productRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void addFavourite() {
        when(userService.getCurrentUser()).thenReturn(user);
        when(productRepository.findById(10)).thenReturn(Optional.of(product));
        Page<Favourite> emptyPage = new PageImpl<>(Collections.emptyList());
        when(favouriteRepository.findByUser(user, Pageable.unpaged())).thenReturn(emptyPage);
        when(favouriteRepository.save(any(Favourite.class))).thenReturn(favourite);
        when(favouriteConverter.toDto(favourite)).thenReturn(favouriteResponseDto);

        FavouriteResponseDto result = favouriteService.addFavourite(10);
        assertEquals(favouriteResponseDto, result);
    }

    @Test
    void addFavouriteWhenProductIsNull() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> favouriteService.addFavourite(null));
        assertEquals("Product Id cannot be null", exception.getMessage());
        verifyNoInteractions(userService, productRepository, favouriteRepository, favouriteConverter);
    }

    @Test
    void addFavouriteIfProductNotFound() {
        when(userService.getCurrentUser()).thenReturn(user);
        when(productRepository.findById(10)).thenReturn(Optional.empty());

        assertEquals("Product not found with ID: 10", assertThrows(NotFoundException.class,
                () -> favouriteService.addFavourite(10)).getMessage());
    }
}