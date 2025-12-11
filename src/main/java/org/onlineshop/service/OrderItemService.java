package org.onlineshop.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.onlineshop.dto.orderItem.OrderItemResponseDto;
import org.onlineshop.dto.orderItem.OrderItemUpdateDto;
import org.onlineshop.entity.Order;
import org.onlineshop.entity.OrderItem;
import org.onlineshop.entity.User;
import org.onlineshop.exception.BadRequestException;
import org.onlineshop.exception.NotFoundException;
import org.onlineshop.repository.OrderItemRepository;
import org.onlineshop.repository.OrderRepository;
import org.onlineshop.service.converter.OrderItemConverter;
import org.onlineshop.service.interfaces.OrderItemServiceInterface;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderItemService implements OrderItemServiceInterface {
    private final OrderItemRepository orderItemRepository;
    private final OrderItemConverter orderItemConverter;
    private final UserService userService;
    private final OrderRepository orderRepository;

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
     * @throws NotFoundException   if the order item with the specified ID is not found
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
        log.info("Item {} deleted from order for user {}", orderItemId, userService.getCurrentUser().getUsername());
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
     * including its ID, product ID, quantity, and price at the updated state
     * @throws BadRequestException      if the input data is null or the user is not authorized
     *                                  to modify the item
     * @throws IllegalArgumentException if the provided quantity is less than 1
     * @throws NotFoundException        if no order item is found with the specified ID
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
        if (!orderItem.getOrder().getUser().getUserId().equals(currentUser.getUserId())) {
            throw new BadRequestException("You can't update another user's order");
        }
        if (!orderItem.getOrder().getStatus().equals(Order.Status.PENDING_PAYMENT)) {
            throw new BadRequestException("You can't update an order that is not in PENDING_PAYMENT status");
        }
        orderItem.setQuantity(dto.getQuantity());
        orderItemRepository.save(orderItem);
        log.info("OrderItem {} quantity updated for user {}", orderItem.getOrderItemId(), currentUser.getUsername());
        return orderItemConverter.toDto(orderItem);
    }
}
