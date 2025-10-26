package org.onlineshop.service;

import lombok.RequiredArgsConstructor;
import org.onlineshop.dto.orderItem.OrderItemRequestDto;
import org.onlineshop.dto.orderItem.OrderItemResponseDto;
import org.onlineshop.dto.orderItem.OrderItemUpdateDto;
import org.onlineshop.entity.Order;
import org.onlineshop.entity.OrderItem;
import org.onlineshop.entity.Product;
import org.onlineshop.entity.User;
import org.onlineshop.exception.BadRequestException;
import org.onlineshop.exception.NotFoundException;
import org.onlineshop.repository.OrderItemRepository;
import org.onlineshop.repository.OrderRepository;
import org.onlineshop.repository.ProductRepository;
import org.onlineshop.service.converter.OrderItemConverter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderItemService implements OrderItemServiceInterface {

    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final OrderItemConverter orderItemConverter;
    private final UserService userService;

    @Transactional
    @Override
    public OrderItemResponseDto addItemToOrder(OrderItemRequestDto dto) {

        if (dto.getProductId() == null || dto.getQuantity() == null) {
            throw new IllegalArgumentException("Params cannot be null");
        }
        if (dto == null) {
            throw new BadRequestException("Request cannot be null");
        }
        if (dto.getQuantity() < 1) {
            throw new IllegalArgumentException("Quantity cannot be less than 1");
        }

        User currentUser = userService.getCurrentUser();
        Order currentOrder = getCurrentOrder(currentUser);
        Integer quantity = dto.getQuantity();

        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new NotFoundException("Product not found with ID: " + dto.getProductId()));

        BigDecimal priceAtPurchase = product.getDiscountPrice() != null
                ? product.getDiscountPrice()
                : product.getPrice();

        OrderItem orderItem = OrderItem.builder()
                .order(currentOrder)
                .product(product)
                .quantity(quantity)
                .priceAtPurchase(priceAtPurchase)
                .build();

        currentOrder.getOrderItems().add(orderItem);
        OrderItem savedOrderItem = orderItemRepository.save(orderItem);
        return orderItemConverter.fromEntity(savedOrderItem);
    }

    @Transactional
    @Override
    public void deleteItemFromOrder(Integer orderItemId) {
        if (orderItemId == null) {
            throw new BadRequestException("OrderItem ID cannot be null");
        }
        OrderItem orderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new NotFoundException("OrderItem not found with ID: " + orderItemId));

        Order currentOrder = orderItem.getOrder();
        currentOrder.getOrderItems().remove(orderItem); // orphanRemoval сработает
        orderItemRepository.delete(orderItem);

    }

    @Transactional
    @Override
    public OrderItemResponseDto updateItemQuantityInOrder(OrderItemUpdateDto dto) {
        if (dto == null) {
            throw new BadRequestException("Request cannot be null");
        }

        OrderItem orderItem = orderItemRepository.findById(dto.getOrderItemId())
                .orElseThrow(() -> new NotFoundException("OrderItem not found with ID: " + dto.getOrderItemId()));
        if (dto.getQuantity() < 1) {
            throw new IllegalArgumentException("Quantity cannot be less than 1");
        }
        orderItem.setQuantity(dto.getQuantity());
        orderItemRepository.save(orderItem);

        return orderItemConverter.fromEntity(orderItem);
    }


    private Order getCurrentOrder(User user) {
        return user.getOrders().stream()
                .filter(o -> o.getStatus().equals(Order.Status.OPEN))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("No opened order found for current user"));
    }
}
