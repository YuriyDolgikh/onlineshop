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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.yml")
class FavouriteServiceGetFavouritesTest {
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
        when(favouriteRepository.findByUser(user)).thenReturn(List.of(favourite));
        when(favouriteConverter.toDtos(List.of(favourite))).thenReturn(List.of(favouriteResponseDto));

        List<FavouriteResponseDto> result = favouriteService.getFavourites();

        assertEquals(1, result.size());
        assertEquals(favouriteResponseDto, result.get(0));

        verify(userService).getCurrentUser();
        verify(favouriteRepository).findByUser(user);
        verify(favouriteConverter).toDtos(List.of(favourite));
    }

    @Test
    void getFavouritesEmpty() {
        when(userService.getCurrentUser()).thenReturn(user);
        when(favouriteRepository.findByUser(user)).thenReturn(List.of());
        when(favouriteConverter.toDtos(List.of())).thenReturn(List.of());

        List<FavouriteResponseDto> result = favouriteService.getFavourites();
        assertEquals(0, result.size());

        verify(userService).getCurrentUser();
        verify(favouriteRepository).findByUser(user);
        verify(favouriteConverter).toDtos(List.of());
    }

    @Test
    void getFavouritesReturnsAllItems() {
        Favourite favourite2 = new Favourite();
        favourite2.setFavouriteId(101);
        favourite2.setUser(user);
        FavouriteResponseDto dto2 = new FavouriteResponseDto(101, "testProduct1");

        when(userService.getCurrentUser()).thenReturn(user);
        when(favouriteRepository.findByUser(user)).thenReturn(List.of(favourite,favourite2));
        when(favouriteConverter.toDtos(List.of(favourite, favourite2))).thenReturn(List.of(favouriteResponseDto,dto2));

        List<FavouriteResponseDto> result = favouriteService.getFavourites();
        assertEquals(2, result.size());
        assertEquals(favouriteResponseDto, result.get(0));
        assertEquals(dto2, result.get(1));
    }
}