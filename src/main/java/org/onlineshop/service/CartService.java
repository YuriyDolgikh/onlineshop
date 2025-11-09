package org.onlineshop.service;

import lombok.RequiredArgsConstructor;
import org.onlineshop.dto.cart.CartResponseDto;
import org.onlineshop.dto.cartItem.CartItemResponseDto;
import org.onlineshop.entity.*;
import org.onlineshop.exception.BadRequestException;
import org.onlineshop.repository.CartRepository;
import org.onlineshop.repository.OrderRepository;
import org.onlineshop.repository.UserRepository;
import org.onlineshop.service.converter.CartItemConverter;
import org.onlineshop.service.interfaces.CartServiceInterface;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CartService implements CartServiceInterface {
    private final OrderRepository orderRepository;
    private final UserService userService;
    private final CartItemConverter cartItemConverter;
    private final CartRepository cartRepository;
    private final UserRepository userRepository;

    @Transactional
    @Override
    public void clearCart() {
        User user = userService.getCurrentUser();
        user.getCart().getCartItems().clear();
        userRepository.save(user);
    }

    @Transactional
    @Override
    public void transferCartToOrder() {

        Cart cart = getCurrentCart();
        User user = userService.getCurrentUser();
        Set<CartItem> cartItems = cart.getCartItems();

        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = cartItemConverter.cartItemToOrderItem(cartItem);
            orderItems.add(orderItem);
        }
        Order newOrder = new Order();
        LocalDateTime now = LocalDateTime.now();
        newOrder.setOrderItems(orderItems);
        newOrder.setUser(user);
        newOrder.setStatus(Order.Status.PENDING_PAYMENT);
        newOrder.setCreatedAt(now);
        newOrder.setUpdatedAt(now);
        Order savedOrder = orderRepository.save(newOrder);
        user.getOrders().add(savedOrder);
        userRepository.save(user);
        clearCart();
    }

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
            BigDecimal itemTotal = product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            BigDecimal itemTotalWithDiscount = itemTotal
                    .subtract(product.getPrice()
                            .multiply(product.getDiscountPrice())
                            .divide(BigDecimal.valueOf(100)));
            totalPrice = totalPrice.add(itemTotalWithDiscount);
        }
        return CartResponseDto.builder()
                .userId(user.getUserId())
                .cartItems(cartItemDtos)
                .totalPrice(totalPrice)
                .build();
    }

    @Transactional
    public Cart getCurrentCart() {
        User user = userService.getCurrentUser();
        return cartRepository.findByUser(user).orElseThrow(() -> new BadRequestException("Cart is empty"));
    }

    @Transactional
    public void saveCart(Cart cart) {
        if (cart == null) {
            throw new IllegalArgumentException("Cart can't be null");
        }
        if (cart.getUser() == null) {
            throw new IllegalArgumentException("User can't be null");
        }
        if (cart.getCartItems() == null){
            throw new IllegalArgumentException("Cart items can't be null");
        }
        cartRepository.save(cart);
    }
}
