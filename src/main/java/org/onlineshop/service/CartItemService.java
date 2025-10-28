package org.onlineshop.service;

import lombok.RequiredArgsConstructor;
import org.onlineshop.dto.cartItem.CartItemResponseDto;
import org.onlineshop.entity.Order;
import org.onlineshop.entity.OrderItem;
import org.onlineshop.entity.Product;
import org.onlineshop.entity.User;
import org.onlineshop.repository.OrderItemRepository;
import org.onlineshop.repository.OrderRepository;
import org.onlineshop.repository.ProductRepository;
import org.onlineshop.service.converter.CartItemConverter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CartItemService implements CartItemServiceInterface {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserService userService;
    private final CartItemConverter cartItemConverter;

    @Transactional
    @Override
    public CartItemResponseDto addItemToCart(Integer productId, Integer quantity) {
        if (productId == null) {
            throw new IllegalArgumentException("Product Id cannot be null");
        }
        if (quantity == null || quantity < 1) {
            throw new IllegalArgumentException("Quantity must be at least 1");
        }
        User user = userService.getCurrentUser();
        OrderItem orderItem;
        try {
            orderItem = getOrderItemFromCart(user, productId);
            orderItem.setQuantity(orderItem.getQuantity() + quantity);
        } catch (IllegalArgumentException e) {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + productId));

            Order openOrder = getCurrentCart(user);

            orderItem = new OrderItem();
            orderItem.setOrder(openOrder);
            orderItem.setProduct(product);
            orderItem.setQuantity(quantity);

        }

        OrderItem savedItem = orderItemRepository.save(orderItem);
        return cartItemConverter.fromEntity(savedItem);
    }

    @Transactional
    @Override
    public CartItemResponseDto removeItemFromCart(Integer productId) {
        if (productId == null) {
            throw new IllegalArgumentException("Product Id cannot be null");
        }
        User user = userService.getCurrentUser();
        OrderItem orderItem = getOrderItemFromCart(user, productId);

        orderItemRepository.delete(orderItem);
        return cartItemConverter.fromEntity(orderItem);
    }

    @Transactional
    @Override
    public CartItemResponseDto updateItemInCart(Integer productId, Integer quantity) {
        if (productId == null) {
            throw new IllegalArgumentException("Product Id cannot be null");
        }
        if (quantity == null || quantity < 1) {
            throw new IllegalArgumentException("Quantity must be at least 1");
        }
        User user = userService.getCurrentUser();
        OrderItem orderItem = getOrderItemFromCart(user, productId);

        orderItem.setQuantity(quantity);
        OrderItem savedItem = orderItemRepository.save(orderItem);

        return cartItemConverter.fromEntity(savedItem);
    }

    @Transactional
    @Override
    public List<CartItemResponseDto> getCartItems() {
        User user = userService.getCurrentUser();
        Order openOrder = getCurrentCart(user);
        return orderItemRepository.findByOrder(openOrder).stream()
                .map(cartItemConverter::fromEntity)
                .collect(Collectors.toList());
    }

    //возвращает корзину пользователя.
    private Order getCurrentCart(User user) {
        return orderRepository.findByUser(user).stream()
                .filter(order -> order.getStatus() == Order.Status.OPEN)
                .findFirst()
                .orElseGet(() -> {
                    Order newOrder = new Order();
                    newOrder.setUser(user);
                    newOrder.setStatus(Order.Status.OPEN);
                    return orderRepository.save(newOrder);
                });
    }

    //возвращает конкретный товар из корзины.
    private OrderItem getOrderItemFromCart(User user, Integer productId) {
        Order openOrder = getCurrentCart(user);

        return orderItemRepository.findByOrder(openOrder).stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Product not found in cart"));
    }
}
