package org.onlineshop.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FavouriteService implements FavouriteServiceInterface {
    private final FavouriteRepository favouriteRepository;
    private final UserService userService;
    private final ProductRepository productRepository;
    private final FavouriteConverter favouriteConverter;

    /**
     * Adds a product to the user's favourite list.
     *
     * @param productId the ID of the product to be added to the favourites. Must not be null.
     * @return a FavouriteResponseDto containing details about the newly added favourite.
     * @throws IllegalArgumentException if the provided productId is null.
     * @throws NotFoundException        if the product with the given ID is not found.
     * @throws BadRequestException      if the product is already in the user's favourites.
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

        Favourite favourite = Favourite.builder()
                .user(user)
                .product(product)
                .build();

        try {
            Favourite savedFavourite = favouriteRepository.save(favourite);
            log.info("Product {} added to favourites for user {}", product.getName(), user.getUsername());
            return favouriteConverter.toDto(savedFavourite);
        } catch (DataIntegrityViolationException exception) {
            throw new BadRequestException("Product is already in favourites");
        }
    }

    /**
     * Deletes a product from the user's favourite list.
     * If the product is not found in the user's favourites, an exception is thrown.
     *
     * @param productId the ID of the product to be deleted from the favourites. Must not be null.
     * @return a FavouriteResponseDto containing details about the deleted favourite.
     * @throws IllegalArgumentException if the provided productId is null.
     * @throws NotFoundException        if the product with the given ID is not found in the user's favourites.
     */
    @Transactional
    @Override
    public FavouriteResponseDto deleteFavourite(Integer productId) {
        if (productId == null) {
            throw new IllegalArgumentException("Product Id cannot be null");
        }

        User user = userService.getCurrentUser();

        Favourite favourite = favouriteRepository
                .findByUserAndProduct(user, productRepository.findById(productId)
                        .orElseThrow(() -> new NotFoundException("Product not found")))
                .orElseThrow(() -> new NotFoundException("Product not found in favourites"));

        favouriteRepository.delete(favourite);
        log.info("Product {} removed from favourites for user {}", favourite.getProduct().getName(), user.getUsername());
        return favouriteConverter.toDto(favourite);
    }

    /**
     * Retrieves the list of the current user's favourite products.
     *
     * @return a list of FavouriteResponseDto objects representing the user's favourite products.
     */
    @Transactional(readOnly = true)
    @Override
    public Page<FavouriteResponseDto> getFavourites(Pageable pageable) {
        User user = userService.getCurrentUser();
        Page<Favourite> favourites = favouriteRepository.findByUser(user, pageable);

        return favourites.map(favouriteConverter::toDto);
    }
}
