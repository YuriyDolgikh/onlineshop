package org.onlineshop.service;

import lombok.RequiredArgsConstructor;
import org.onlineshop.dto.cart.CartResponseDto;
import org.onlineshop.dto.cartItem.CartItemResponseDto;
import org.onlineshop.entity.Order;
import org.onlineshop.entity.OrderItem;
import org.onlineshop.entity.User;
import org.onlineshop.repository.OrderItemRepository;
import org.onlineshop.repository.OrderRepository;
import org.onlineshop.service.converter.CartItemConverter;
import org.onlineshop.service.interfaces.CartServiceInterface;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService implements CartServiceInterface {
    private final OrderRepository orderRepository;
    private final UserService userService;
    private final OrderItemRepository orderItemRepository;
    private final CartItemConverter cartItemConverter;

    @Transactional
    @Override
    public void clearCart() {
        User user = userService.getCurrentUser();
        Order openOrder = getCurrentCart(user);

        List<OrderItem> items = orderItemRepository.findByOrder(openOrder);
        orderItemRepository.deleteAll(items);
    }

    @Transactional
    @Override
    public void transferToOrder() {
        User user = userService.getCurrentUser();
        Order openOrder = getCurrentCart(user);
        List<OrderItem> items = orderItemRepository.findByOrder(openOrder);
        if (items.isEmpty()) {
            throw new IllegalStateException("Cart is empty, cannot transfer to order");
        }

        openOrder.setStatus(Order.Status.PENDING_PAYMENT);
        orderRepository.save(openOrder);
    }

    @Transactional
    @Override
    public CartResponseDto getCartFullData() {
        User user = userService.getCurrentUser();
        Order openOrder = getCurrentCart(user);

        List<OrderItem> items = orderItemRepository.findByOrder(openOrder);

        List<CartItemResponseDto> cartItems = items.stream()
                .map(cartItemConverter::toDto)
                .collect(Collectors.toList());

        BigDecimal totalPrice = BigDecimal.ZERO;
        for (CartItemResponseDto item : cartItems) {
            BigDecimal itemTotal = item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            totalPrice = totalPrice.add(itemTotal);
        }
        return CartResponseDto.builder()
                .userId(user.getUserId())
                .cartItems(cartItems)
                .totalPrice(totalPrice)
                .build();
    }

    private Order getCurrentCart(User user) {
        return orderRepository.findByUser(user).stream()
                .filter(o -> o.getStatus() == Order.Status.OPEN)
                .findFirst()
                .orElseGet(() -> {
                    Order newOrder = new Order();
                    newOrder.setUser(user);
                    newOrder.setStatus(Order.Status.OPEN);
                    return orderRepository.save(newOrder);
                });
    }
}
