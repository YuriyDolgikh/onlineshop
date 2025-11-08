package org.onlineshop.service;

import lombok.RequiredArgsConstructor;
import org.onlineshop.dto.favourite.FavouriteResponseDto;
import org.onlineshop.entity.Favourite;
import org.onlineshop.entity.Product;
import org.onlineshop.entity.User;
import org.onlineshop.exception.BadRequestException;
import org.onlineshop.exception.NotFoundException;
import org.onlineshop.repository.FavouriteRepository;
import org.onlineshop.repository.ProductRepository;
import org.onlineshop.service.converter.FavouriteConverter;
import org.onlineshop.service.interfaces.FavouriteServiceInterface;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FavouriteService implements FavouriteServiceInterface {
    private final FavouriteRepository favouriteRepository;
    private final UserService userService;
    private final ProductRepository productRepository;
    private final FavouriteConverter favouriteConverter;

    @Transactional
    @Override
    public FavouriteResponseDto addFavourite(Integer productId) {
        if (productId == null) {
            throw new IllegalArgumentException("Product Id cannot be null");
        }
        User user = userService.getCurrentUser();
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product not found with ID: " + productId));
        boolean exist = favouriteRepository.findByUser(user).stream()
                .anyMatch(f -> f.getProduct().getId().equals(product.getId()));
        if (exist) {
            throw new BadRequestException("Product is already in favourites");
        }

        Favourite favourite = new Favourite();
        favourite.setUser(user);
        favourite.setProduct(product);
        Favourite savedFavourite = favouriteRepository.save(favourite);

        return favouriteConverter.toDto(savedFavourite);
    }

    @Transactional
    @Override
    public FavouriteResponseDto deleteFavourite(Integer productId) {
        if (productId == null) {
            throw new IllegalArgumentException("Product Id cannot be null");
        }

        User user = userService.getCurrentUser();
        Favourite favourite = favouriteRepository.findByUser(user).stream()
                .filter(f -> f.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Product not found in favourites"));
        favouriteRepository.delete(favourite);

        return favouriteConverter.toDto(favourite);
    }

    @Transactional
    @Override
    public List<FavouriteResponseDto> getFavourites() {
        User user = userService.getCurrentUser();
        List<Favourite> favourites = favouriteRepository.findByUser(user);

        return favouriteConverter.toDtos(favourites);
    }
}
