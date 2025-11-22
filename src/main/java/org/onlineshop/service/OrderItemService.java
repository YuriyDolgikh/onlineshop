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
import org.onlineshop.service.interfaces.OrderItemServiceInterface;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * Service class handling operations related to order items, including adding items
 * to orders, updating their quantities, and deleting them. Ensures validation of
 * input data and user authorization before performing operations.
 *
 * This class works with various repositories and services to manage interactions
 * with the database and user-related operations. It also leverages converters to
 * handle mappings between DTOs and entity objects.
 *
 * Annotated with `@Service` to indicate that it is a service layer class, and
 * `@RequiredArgsConstructor` to enable constructor-based dependency injection.
 */
@Service
@RequiredArgsConstructor
public class OrderItemService implements OrderItemServiceInterface {
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final OrderItemConverter orderItemConverter;
    private final UserService userService;
    private final OrderRepository orderRepository;

    /**
     * Adds an item to the current order based on the provided request data.
     *
     * @param dto the data transfer object containing the details of the product and quantity to add to the order;
     *            must not be null and must contain valid product ID and quantity (minimum value of 1)
     * @return the response data transfer object containing the details of the added order item,
     *         including its ID, product ID, quantity, and price at purchase
     * @throws BadRequestException if the request data is null
     * @throws IllegalArgumentException if the product ID or quantity is null, or if the quantity is less than 1
     * @throws NotFoundException if the product with the specified ID is not found
     */
    @Transactional
    @Override
    public OrderItemResponseDto addItemToOrder(OrderItemRequestDto dto) {

        if (dto == null) {
            throw new BadRequestException("Request cannot be null");
        }
        if (dto.getProductId() == null || dto.getQuantity() == null) {
            throw new IllegalArgumentException("Params cannot be null");
        }
        if (dto.getQuantity() < 1) {
            throw new IllegalArgumentException("Quantity cannot be less than 1");
        }
        Order currentOrder = getCurrentOrder();
        Integer quantity = dto.getQuantity();

        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new NotFoundException("Product not found with ID: " + dto.getProductId()));

        BigDecimal price = product.getPrice();
        BigDecimal priceAtPurchase = !product.getDiscountPrice().equals(BigDecimal.ZERO)
                ? price.multiply(product.getDiscountPrice()).divide(new BigDecimal(100))
                : product.getPrice();

        OrderItem orderItem = OrderItem.builder()
                .order(currentOrder)
                .product(product)
                .quantity(quantity)
                .priceAtPurchase(priceAtPurchase)
                .build();

        currentOrder.getOrderItems().add(orderItem);
        OrderItem savedOrderItem = orderItemRepository.save(orderItem);
        return orderItemConverter.toDto(savedOrderItem);
    }

    /**
     * Deletes an item from the specified order. The order item is identified
     * by its unique ID. This method ensures that the order is in a modifiable
     * state (e.g., `PENDING_PAYMENT`) and that the current user is authorized
     * to delete the item. If these conditions are not met, an exception is thrown.
     *
     * @param orderItemId the unique identifier of the order item to be deleted;
     *                    must not be null
     * @throws BadRequestException if the orderItemId is null, if the order
     *                             is not in `PENDING_PAYMENT` status, or if
     *                             the current user is not authorized to modify the order
     * @throws NotFoundException if the order item with the specified ID is not found
     */
    @Transactional
    @Override
    public void deleteItemFromOrder(Integer orderItemId) {
        if (orderItemId == null) {
            throw new BadRequestException("OrderItem ID cannot be null");
        }
        OrderItem orderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new NotFoundException("OrderItem not found with ID: " + orderItemId));

        Order currentOrder = orderItem.getOrder();
        if (!currentOrder.getStatus().equals(Order.Status.PENDING_PAYMENT)) {
            throw new BadRequestException("You can't delete an order that is not in PENDING_PAYMENT status");
        }
        if (!currentOrder.getUser().getUserId().equals(userService.getCurrentUser().getUserId())) {
            throw new BadRequestException("You can't delete another user's order");
        }
        currentOrder.getOrderItems().remove(orderItem);
        orderItemRepository.delete(orderItem);
        orderRepository.save(currentOrder);
    }

    /**
     * Updates the quantity of an item in a specific order based on the provided data.
     * Validates the input and ensures that the user is authorized to update the item.
     * If the validation passes, the item's quantity is updated in the database.
     *
     * @param dto the data transfer object containing the details for the update, including
     *            the order item ID and the new quantity; must not be null and the quantity
     *            must be at least 1
     * @return the response data transfer object containing the updated details of the order item,
     *         including its ID, product ID, quantity, and price at the updated state
     * @throws BadRequestException if the input data is null or the user is not authorized
     *                             to modify the item
     * @throws IllegalArgumentException if the provided quantity is less than 1
     * @throws NotFoundException if no order item is found with the specified ID
     */
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
        User currentUser = userService.getCurrentUser();
        if (!orderItem.getOrder().getUser().getUserId().equals(currentUser.getUserId())){
            throw new BadRequestException("You can't update another user's order");
        }
        if (!orderItem.getOrder().getStatus().equals(Order.Status.PENDING_PAYMENT)) {
            throw new BadRequestException("You can't update an order that is not in PENDING_PAYMENT status");
        }
        orderItem.setQuantity(dto.getQuantity());
        orderItemRepository.save(orderItem);
        return orderItemConverter.toDto(orderItem);
    }

    /**
     * Retrieves the current pending payment order for the currently authenticated user.
     *
     * The method gets the currently authenticated user and searches through their orders
     * to find an order with the status of PENDING_PAYMENT. If no such order is found,
     * a NotFoundException is thrown.
     *
     * @return the current order with a status of PENDING_PAYMENT for the current user
     * @throws NotFoundException if no order with PENDING_PAYMENT status is found
     */
    private Order getCurrentOrder() {
        User user = userService.getCurrentUser();
        return user.getOrders().stream()
                .filter(o -> o.getStatus().equals(Order.Status.PENDING_PAYMENT))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("No opened order found for current user"));
    }
}
