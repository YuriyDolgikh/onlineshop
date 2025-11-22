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

/**
 * Service for managing user's favourite products.
 * Provides functionality to add, delete, and retrieve user favourites.
 */
@Service
@RequiredArgsConstructor
public class FavouriteService implements FavouriteServiceInterface {
    private final FavouriteRepository favouriteRepository;
    private final UserService userService;
    private final ProductRepository productRepository;
    private final FavouriteConverter favouriteConverter;

    /**
     * Adds a product to the user's favourites list.
     *
     * @param productId the ID of the product to be added to the favourites. Must not be null.
     * @return a FavouriteResponseDto containing details about the newly added favourite.
     * @throws IllegalArgumentException if the provided productId is null.
     * @throws NotFoundException if the product with the given ID is not found.
     * @throws BadRequestException if the product is already in the user's favourites.
     */
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

    /**
     * Deletes a product from the user's favourites list.
     * If the product is not found in the user's favourites, an exception is thrown.
     *
     * @param productId the ID of the product to be deleted from the favourites. Must not be null.
     * @return a FavouriteResponseDto containing details about the deleted favourite.
     * @throws IllegalArgumentException if the provided productId is null.
     * @throws NotFoundException if the product with the given ID is not found in the user's favourites.
     */
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

    /**
     * Retrieves the list of the current user's favourite products.
     *
     * @return a list of FavouriteResponseDto objects representing the user's favourite products.
     */
    @Transactional
    @Override
    public List<FavouriteResponseDto> getFavourites() {
        User user = userService.getCurrentUser();
        List<Favourite> favourites = favouriteRepository.findByUser(user);

        return favouriteConverter.toDtos(favourites);
    }
}
