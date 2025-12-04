package org.onlineshop.service;

import lombok.RequiredArgsConstructor;
import org.onlineshop.dto.cartItem.*;
import org.onlineshop.entity.Cart;
import org.onlineshop.entity.CartItem;
import org.onlineshop.entity.Product;
import org.onlineshop.entity.User;
import org.onlineshop.exception.BadRequestException;
import org.onlineshop.exception.NotFoundException;
import org.onlineshop.repository.CartItemRepository;
import org.onlineshop.service.converter.CartItemConverter;
import org.onlineshop.service.interfaces.CartItemServiceInterface;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

/**
 * Service class responsible for managing cart items for specific users.
 * Provides functionality to add, remove, update, and retrieve cart items
 * within a user's shopping cart. This class handles business logic
 * related to cart item persistence and integration with upstream services
 * such as ProductService, UserService, and CartService.
 */
@Service
@RequiredArgsConstructor
public class CartItemService implements CartItemServiceInterface {

    private final CartItemRepository cartItemRepository;
    private final UserService userService;
    private final CartItemConverter cartItemConverter;
    private final CartService cartService;
    private final ProductService productService;

    /**
     * Adds an item to the user's cart. If the item already exists in the cart, its quantity is updated.
     * If the item is not present in the cart, it is added as a new entry.
     *
     * @param cartItemRequestDto the DTO containing information about the product to add to the cart, such as product ID and quantity.
     * @return a DTO representing the added or updated cart item, including product details and quantity.
     * @throws IllegalArgumentException if the product ID or quantity is null.
     * @throws BadRequestException      if the quantity is less than 1, or if the product cannot be found.
     */
    @Transactional
    @Override
    public CartItemSimpleResponseDto addItemToCart(CartItemRequestDto cartItemRequestDto) {
        if (cartItemRequestDto.getProductId() == null) {
            throw new IllegalArgumentException("Product Id cannot be null");
        }
        if (cartItemRequestDto.getQuantity() == null) {
            throw new IllegalArgumentException("Quantity cannot be null");
        }
        if (cartItemRequestDto.getQuantity() < 1) {
            throw new BadRequestException("Quantity must be at least 1");
        }
        if (productService.getProductById(cartItemRequestDto.getProductId()).isEmpty()) {
            throw new BadRequestException("Product with ID: " + cartItemRequestDto.getProductId() + " not found");
        }
        User user = userService.getCurrentUser();
        Cart cart = user.getCart();
        Set<CartItem> cartItems = cart.getCartItems();
        Optional<CartItem> existingCartItem = getCartItemFromCart(cartItemRequestDto.getProductId());
        CartItem savedCartItem;
        if (existingCartItem.isPresent()) {
            CartItem cartItem = existingCartItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + cartItemRequestDto.getQuantity());
            savedCartItem = cartItemRepository.save(cartItem);
        } else {
            Product product = productService.getProductById(cartItemRequestDto.getProductId())
                    .orElseThrow(() -> new BadRequestException("Product not found"));

            CartItem newCartItem = CartItem.builder()
                    .product(product)
                    .quantity(cartItemRequestDto.getQuantity())
                    .cart(cart)
                    .build();

            savedCartItem = cartItemRepository.save(newCartItem);
            cartItems.add(savedCartItem);
        }

        cartService.saveCart(cart);
        return cartItemConverter.toSimpleDto(savedCartItem);
    }

    /**
     * Removes an item from the user's cart based on the provided product ID.
     * If the product is not found in the cart, an exception is thrown.
     * The cart is then updated and persisted.
     *
     * @param productId the ID of the product to be removed from the cart
     * @return a DTO representing the cart item that was removed, including product details and quantity
     * @throws IllegalArgumentException if the product ID is null
     * @throws NotFoundException        if the product with the specified ID cannot be found in the cart
     */
    @Transactional
    @Override
    public CartItemResponseDto removeItemFromCart(Integer productId) {
        if (productId == null) {
            throw new IllegalArgumentException("Product Id cannot be null");
        }
        Cart cart = cartService.getCurrentCart();
        CartItem cartItemToRemove = cart.getCartItems().stream()
                .filter(cartItem -> cartItem.getProduct().getId().equals(productId))
                .findFirst().orElseThrow(() -> new NotFoundException("Product with ID: " + productId + " not found in cart"));
        cart.getCartItems().remove(cartItemToRemove);
        cartItemRepository.delete(cartItemToRemove);
        cartService.saveCart(cart);
        return cartItemConverter.toDto(cartItemToRemove);
    }

    /**
     * Updates an existing item in the cart with new details provided in the input.
     * Validates the input and ensures that the product exists and is present in the user's cart.
     * If the product is not found in the cart or does not exist, appropriate exceptions are thrown.
     *
     * @param cartItemUpdateDto the DTO containing the product ID and the new quantity to update in the cart
     * @return a data transfer object representing the updated cart item
     * @throws IllegalArgumentException if the product ID or quantity is null, or if the quantity is less than 1
     * @throws NotFoundException        if the product does not exist in the system or is not found in the user's cart
     */
    @Transactional
    @Override
    public CartItemResponseDto updateItemInCart(CartItemUpdateDto cartItemUpdateDto) {
        if (cartItemUpdateDto.getProductId() == null) {
            throw new IllegalArgumentException("Product Id cannot be null");
        }
        if (cartItemUpdateDto.getQuantity() == null) {
            throw new IllegalArgumentException("Quantity cannot be null");
        }
        if (cartItemUpdateDto.getQuantity() < 1) {
            throw new BadRequestException("Quantity must be at least 1");
        }
        if (productService.getProductById(cartItemUpdateDto.getProductId()).isEmpty()) {
            throw new NotFoundException("Product with ID: " + cartItemUpdateDto.getProductId() + " not found");
        }
        CartItem cartItemToUpdate = getCartItemFromCart(cartItemUpdateDto.getProductId()).orElseThrow(() ->
                new NotFoundException("Product with ID: " + cartItemUpdateDto.getProductId() + " not found in users cart"));
        cartItemToUpdate.setQuantity(cartItemUpdateDto.getQuantity());
        CartItem savedCartItem = cartItemRepository.save(cartItemToUpdate);
        return cartItemConverter.toDto(savedCartItem);
    }

    /**
     * Retrieves the items in the current user's shopping cart.
     * Converts the cart items to a set of CartItemResponseDto.
     *
     * @return a set of CartItemResponseDto representing the items in the cart
     */
    @Transactional(readOnly = true)
    @Override
    @Lazy
    public Set<CartItemFullResponseDto> getCartItems() {
        Cart cart = cartService.getCurrentCart();
        Set<CartItem> cartItems = cart.getCartItems();
        return cartItemConverter.toFullDtos(cartItems);
    }

    /**
     * Retrieves a specific CartItem from the current user's cart based on the provided product ID.
     *
     * @param productId the ID of the product to search for within the user's cart
     * @return an Optional containing the CartItem if found; otherwise, an empty Optional
     */
    @Transactional(readOnly = true)
     public Optional<CartItem> getCartItemFromCart(Integer productId) {
        User user = userService.getCurrentUser();
        Cart userCart = user.getCart();
        Set<CartItem> cartItems = userCart.getCartItems();
        return cartItems.stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst();
    }
}
