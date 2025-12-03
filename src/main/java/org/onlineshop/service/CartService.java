package org.onlineshop.service;

import lombok.RequiredArgsConstructor;
import org.onlineshop.dto.cart.CartResponseDto;
import org.onlineshop.dto.cartItem.CartItemResponseDto;
import org.onlineshop.dto.cartItem.CartItemSympleResponseDto;
import org.onlineshop.entity.*;
import org.onlineshop.exception.BadRequestException;
import org.onlineshop.exception.NotFoundException;
import org.onlineshop.repository.CartRepository;
import org.onlineshop.repository.OrderRepository;
import org.onlineshop.service.converter.CartItemConverter;
import org.onlineshop.service.interfaces.CartServiceInterface;
import org.onlineshop.service.interfaces.UserServiceInterface;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CartService implements CartServiceInterface {
    private final OrderRepository orderRepository;
    private final UserServiceInterface userService;
    private final CartItemConverter cartItemConverter;
    private final CartRepository cartRepository;

    /**
     * Clears the current user's shopping cart.
     */
    @Transactional
    @Override
    public void clearCart() {
        User user = userService.getCurrentUser();
        user.getCart().getCartItems().clear();
        userService.saveUser(user);
    }

    /**
     * Transfers the contents of the current user's shopping cart to a new order
     * and updates the order repository and user information.
     * <p>
     * This method performs the following steps:
     * 1. Retrieves the current cart and validates it is not empty.
     * 2. Converts cart items to order items while validating the presence of product discounts.
     * 3. Checks that the user does not already have an order in the PENDING_PAYMENT status.
     * 4. Creates a new order with the appropriate status and delivery method and saves it to the repository.
     * 5. Associates the converted order items with the newly created order.
     * 6. Updates the user's order list and persists the changes.
     *
     * @throws BadRequestException if the cart is empty, any product lacks a discount price,
     *                             or if the user already has an order in PENDING_PAYMENT status.
     */
    @Transactional
    @Override
    public void transferCartToOrder() {

        Cart cart = getCurrentCart();
        Set<CartItem> cartItems = cart.getCartItems();
        if (cartItems.isEmpty()) {
            throw new BadRequestException("User's cart is empty. Nothing to transfer.");
        }
        User user = userService.getCurrentUser();

        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItem cartItem : cartItems) {
            if (cartItem.getProduct().getDiscountPrice() == null) {
                throw new BadRequestException("Product discount cannot be null.");
            }
            OrderItem orderItem = cartItemConverter.cartItemToOrderItem(cartItem);
            orderItems.add(orderItem);
        }
        if (orderItems.isEmpty()) {
            throw new NotFoundException("Product list is empty. Unable to create empty order.");
        }
        Order currentOrder = orderRepository.findByUserAndStatus(user, Order.Status.PENDING_PAYMENT);
        if (currentOrder != null) {
            throw new BadRequestException("User already has an order in PENDING_PAYMENT status.");
        }
        Order newOrder = new Order();
        LocalDateTime now = LocalDateTime.now();
        newOrder.setUser(user);
        newOrder.setStatus(Order.Status.PENDING_PAYMENT);
        newOrder.setDeliveryMethod(Order.DeliveryMethod.PICKUP);
        newOrder.setCreatedAt(now);
        newOrder.setUpdatedAt(now);
        Order savedOrder = orderRepository.save(newOrder);
        orderItems.forEach(oi -> oi.setOrder(savedOrder));
        savedOrder.setOrderItems(orderItems);
        orderRepository.save(savedOrder);
        user.getOrders().add(savedOrder);
        userService.saveUser(user);
    }

    /**
     * Retrieves the full data of the current user's shopping cart, including cart items
     * and the total price with applied discounts.
     *
     * @return a {@link CartResponseDto} containing the user ID, a list of cart item details,
     * and the total price of the items in the cart with discounts applied.
     */
    @Transactional
    @Override
    public CartResponseDto getCartFullData() {
        User user = userService.getCurrentUser();
        Cart cart = getCurrentCart();
        Set<CartItem> items = cart.getCartItems();
        List<CartItemResponseDto> cartItemDtos = items.stream()
                .map(cartItemConverter::toDto)
                .toList();
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (CartItemResponseDto item : cartItemDtos) {
            Product product = item.getProduct();

            BigDecimal quantity = BigDecimal.valueOf(item.getQuantity());

            BigDecimal price = product.getPrice();

            BigDecimal discount = product.getDiscountPrice();

            BigDecimal discountedPrice = price.subtract(discount).setScale(2, RoundingMode.HALF_UP);

            BigDecimal itemTotal = discountedPrice.multiply(quantity).setScale(2, RoundingMode.HALF_UP);

            totalPrice = totalPrice.add(itemTotal);
        }

        List<CartItemSympleResponseDto> cartItemSimpleDtos = cartItemDtos
                .stream().map(o -> cartItemConverter.toSympleDtoFromDto(o)).toList();

        return CartResponseDto.builder()
                .userId(user.getUserId())
                .cartSympleItems(cartItemSimpleDtos)
                .totalPrice(totalPrice)
                .build();
    }

    /**
     * Retrieves the current user's shopping cart.
     * If the cart is empty, an exception is thrown.
     *
     * @return the current user's shopping cart object, or an exception if the cart is empty
     */
    @Transactional
    public Cart getCurrentCart() {
        User user = userService.getCurrentUser();
        if (user.getCart() == null) {
            throw new IllegalArgumentException("Cart cannot be null");
        }
        return cartRepository.findByUser(user).orElseThrow(() -> new BadRequestException("Cart is empty"));
    }

    /**
     * Saves the specified cart entity into the repository after validating its contents.
     * The cart must have a valid user and cart items present; otherwise, an exception is thrown.
     *
     * @param cart the cart object to be saved
     *             must not be null, must have a non-null user, and non-null cart items
     * @return the saved cart object, or an exception if the cart is invalid
     * @throws IllegalArgumentException if the cart is null, the cart's user is null, or the cart's items are null
     */
    @Transactional
    public Cart saveCart(Cart cart) {
        if (cart == null) {
            throw new IllegalArgumentException("Cart can't be null");
        }
        if (cart.getUser() == null) {
            throw new IllegalArgumentException("User can't be null");
        }
        if (cart.getCartItems() == null) {
            throw new IllegalArgumentException("Cart items can't be null");
        }
        return cartRepository.save(cart);
    }
}
