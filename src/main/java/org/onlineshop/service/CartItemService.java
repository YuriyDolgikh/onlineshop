package org.onlineshop.service;

import lombok.RequiredArgsConstructor;
import org.onlineshop.dto.cartItem.CartItemRequestDto;
import org.onlineshop.dto.cartItem.CartItemResponseDto;
import org.onlineshop.dto.cartItem.CartItemUpdateDto;
import org.onlineshop.entity.Cart;
import org.onlineshop.entity.CartItem;
import org.onlineshop.entity.Product;
import org.onlineshop.entity.User;
import org.onlineshop.exception.BadRequestException;
import org.onlineshop.repository.*;
import org.onlineshop.service.converter.CartItemConverter;
import org.onlineshop.service.interfaces.CartItemServiceInterface;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CartItemService implements CartItemServiceInterface {

    private final CartItemRepository cartItemRepository;
    private final UserService userService;
    private final CartItemConverter cartItemConverter;
    private final CartService cartService;
    private final ProductService productService;

    @Transactional
    @Override
    public CartItemResponseDto addItemToCart(CartItemRequestDto cartItemRequestDto) {
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
        Optional<CartItem> cartItem = getCartItemFromCart(cartItemRequestDto.getProductId());
        CartItem savedCartItem = new CartItem();
        if (cartItem.isPresent()) {
            cartItem.get().setQuantity(cartItem.get().getQuantity() + cartItemRequestDto.getQuantity());
        } else {
            Product product = productService.getProductById(cartItemRequestDto.getProductId()).get();
            CartItem newCartItem = CartItem.builder()
                    .product(product)
                    .quantity(cartItemRequestDto.getQuantity())
                    .build();
            savedCartItem = cartItemRepository.save(newCartItem);
            cartItems.add(savedCartItem);
            cart.setCartItems(cartItems);
        }
        cartService.saveCart(cart);
        return cartItemConverter.toDto(savedCartItem);
    }

    @Transactional
    @Override
    public CartItemResponseDto removeItemFromCart(Integer productId) {
        if (productId == null) {
            throw new IllegalArgumentException("Product Id cannot be null");
        }
        Cart cart = cartService.getCurrentCart();
        CartItem cartItemToRemove = cart.getCartItems().stream()
                .filter(cartItem -> cartItem.getProduct().getId().equals(productId))
                .findFirst().orElseThrow(() -> new IllegalArgumentException("Product with ID: " + productId + " not found in cart"));
        cart.getCartItems().remove(cartItemToRemove);
        cartItemRepository.delete(cartItemToRemove);
        cartService.saveCart(cart);
        return cartItemConverter.toDto(cartItemToRemove);
    }

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
            throw new IllegalArgumentException("Quantity must be at least 1");
        }
        if (productService.getProductById(cartItemUpdateDto.getProductId()).isEmpty()) {
            throw new BadRequestException("Product with ID: " + cartItemUpdateDto.getProductId() + " not found");
        }
        CartItem cartItemToUpdate = getCartItemFromCart(cartItemUpdateDto.getProductId()).orElseThrow(() ->
                new IllegalArgumentException("Product with ID: " + cartItemUpdateDto.getProductId() + " not found in users cart"));
        cartItemToUpdate.setQuantity(cartItemUpdateDto.getQuantity());
        CartItem savedCartItem = cartItemRepository.save(cartItemToUpdate);
        return cartItemConverter.toDto(savedCartItem);
    }

    @Transactional
    @Override
    public Set<CartItemResponseDto> getCartItems() {
        Cart cart = cartService.getCurrentCart();
        return cartItemConverter.toDtos(cart.getCartItems());
    }

    //возвращает конкретный товар из корзины текущего пользователя
    private Optional<CartItem> getCartItemFromCart(Integer productId) {
        User user = userService.getCurrentUser();
        Cart userCart = user.getCart();
        Set<CartItem> cartItems = userCart.getCartItems();
        return cartItems.stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst();
    }
}
