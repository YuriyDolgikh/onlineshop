package org.onlineshop.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.onlineshop.dto.favourite.FavouriteResponseDto;
import org.onlineshop.entity.Favourite;
import org.onlineshop.entity.Product;
import org.onlineshop.entity.User;
import org.onlineshop.repository.FavouriteRepository;
import org.onlineshop.repository.ProductRepository;
import org.onlineshop.service.converter.FavouriteConverter;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.yml")
class FavouriteServiceGetFavouritesTest {

    private FavouriteService favouriteService;

    @Mock
    private FavouriteRepository favouriteRepository;

    @Mock
    private UserService userService;

//    @Mock
//    private ProductRepository productRepository;

    @Mock
    private FavouriteConverter favouriteConverter;

    private User user;
//    private Product product;
    private Favourite favourite;
    private FavouriteResponseDto favouriteResponseDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        favouriteService = new FavouriteService(favouriteRepository, userService, null, favouriteConverter);

        user = new User();
        user.setUserId(1);

        favourite = new Favourite();
        favourite.setFavouriteId(100);
        favourite.setUser(user);

        favouriteResponseDto = new FavouriteResponseDto(100, "testProduct");
    }

    @Test
    void getFavouritesWithItems() {
        when(userService.getCurrentUser()).thenReturn(user);
        when(favouriteRepository.findByUser(eq(user), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(favourite)));
        when(favouriteConverter.toDto(favourite)).thenReturn(favouriteResponseDto);

        // Вызов метода
        Page<FavouriteResponseDto> result = favouriteService.getFavourites(Pageable.unpaged());

        // Проверки
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(favouriteResponseDto, result.getContent().get(0));

        // Верификация вызовов
        verify(userService).getCurrentUser();
        verify(favouriteRepository).findByUser(eq(user), any(Pageable.class));
        verify(favouriteConverter).toDto(favourite);
    }

    @Test
    void getFavouritesEmpty() {
        when(userService.getCurrentUser()).thenReturn(user);
        Page<Favourite> emptyPage = new PageImpl<>(List.of());
        when(favouriteRepository.findByUser(eq(user), any(Pageable.class))).thenReturn(emptyPage);

        Page<FavouriteResponseDto> result = favouriteService.getFavourites(Pageable.unpaged());

        assertNotNull(result);
        assertEquals(0, result.getTotalElements());

        verify(userService).getCurrentUser();
        verify(favouriteRepository).findByUser(eq(user), any(Pageable.class));
    }

    @Test
    void getFavouritesReturnsAllItems() {
        Favourite favourite2 = new Favourite();
        favourite2.setFavouriteId(101);
        favourite2.setUser(user);

        FavouriteResponseDto dto2 = new FavouriteResponseDto(101, "testProduct1");

        when(userService.getCurrentUser()).thenReturn(user);
        List<Favourite> favourites = List.of(favourite, favourite2);
        Page<Favourite> favouritesPage = new PageImpl<>(favourites);
        when(favouriteRepository.findByUser(eq(user), any(Pageable.class))).thenReturn(favouritesPage);

        // Мокаем toDto для каждого элемента, так как map вызывает его отдельно
        when(favouriteConverter.toDto(favourite)).thenReturn(favouriteResponseDto);
        when(favouriteConverter.toDto(favourite2)).thenReturn(dto2);

        Page<FavouriteResponseDto> result = favouriteService.getFavourites(Pageable.unpaged());

        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals(favouriteResponseDto, result.getContent().get(0));
        assertEquals(dto2, result.getContent().get(1));

        verify(userService).getCurrentUser();
        verify(favouriteRepository).findByUser(eq(user), any(Pageable.class));
        verify(favouriteConverter).toDto(favourite);
        verify(favouriteConverter).toDto(favourite2);
    }
}