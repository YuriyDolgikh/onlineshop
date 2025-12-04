package org.onlineshop.service;

import lombok.RequiredArgsConstructor;
import org.onlineshop.dto.order.OrderRequestDto;
import org.onlineshop.dto.order.OrderResponseDto;
import org.onlineshop.dto.order.OrderStatusResponseDto;
import org.onlineshop.entity.Order;
import org.onlineshop.entity.OrderItem;
import org.onlineshop.entity.Product;
import org.onlineshop.entity.User;
import org.onlineshop.exception.BadRequestException;
import org.onlineshop.exception.MailSendingException;
import org.onlineshop.exception.NotFoundException;
import org.onlineshop.repository.OrderRepository;
import org.onlineshop.repository.UserRepository;
import org.onlineshop.service.converter.OrderConverter;
import org.onlineshop.service.interfaces.OrderServiceInterface;
import org.onlineshop.service.mail.MailUtil;
import org.onlineshop.service.util.PdfOrderGenerator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService implements OrderServiceInterface {
    private final OrderRepository orderRepository;
    private final UserService userService;
    private final UserRepository userRepository;
    private final OrderConverter orderConverter;
    private final MailUtil mailUtil;
    private final CartService cartService;

    /**
     * Retrieves an order by its ID.
     *
     * @param orderId the ID of the order to be retrieved - must not be null
     * @return an {@link OrderResponseDto} containing details of the retrieved order
     */
    @Override
    @Transactional(readOnly = true)
    public OrderResponseDto getOrderById(Integer orderId) {
        if (!isAccessToOrderAllowed(orderId)) {
            throw new AccessDeniedException("Access denied");
        }
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found with ID: " + orderId));
        return orderConverter.toDto(order);
    }

    /**
     * Retrieves all orders for the specified user.
     *
     * @param userId the ID of the user whose orders are to be retrieved - must not be null
     * @return a list of {@link OrderResponseDto} objects representing the user's orders
     * @throws NotFoundException        if the user with the specified ID is not found
     * @throws AccessDeniedException    if the current user is not authorized to view the orders of another user
     * @throws IllegalArgumentException if the specified user ID is null
     */

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponseDto> getOrdersByUser(Integer userId) {
        if (userId == null) {
            throw new IllegalArgumentException("UserId cannot be null");
        }
        User requestedUser = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with ID: " + userId));

        User currentUser = userService.getCurrentUser();
        if (currentUser.getRole() == User.Role.USER && !currentUser.getUserId().equals(userId)) {
            throw new AccessDeniedException("Access denied");
        }
        List<Order> orders = orderRepository.findByUser(requestedUser);
        return orderConverter.toDtos(orders);
    }

    /**
     * Updates the status of an order.
     *
     * @param orderId   the ID of the order to be updated - must not be null
     * @param newStatus the new status to be set for the order - must not be null or blank
     * @return an {@link OrderResponseDto} containing the updated order details
     * @throws NotFoundException        if the order with the specified ID is not found
     * @throws AccessDeniedException    if the current user is not authorized to update the order
     * @throws IllegalArgumentException if the specified order ID or new status is null or blank
     */
    @Override
    @Transactional
    public OrderResponseDto updateOrderStatus(Integer orderId, String newStatus) {
        if (!isAccessToOrderAllowed(orderId)) {
            throw new AccessDeniedException("Access denied");
        }
        if (newStatus == null || newStatus.isBlank()) {
            throw new BadRequestException("New Status cannot be null or blank");
        }
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found with ID: " + orderId));
        Order.Status updatedStatus = Order.Status.valueOf(newStatus.toUpperCase());
        order.setStatus(updatedStatus);
        orderRepository.save(order);
        return orderConverter.toDto(order);
    }

    /**
     * Cancel an order.
     *
     * @param orderId the ID of the order to be cancelled - must not be null
     * @throws NotFoundException     if the order with the specified ID is not found
     * @throws AccessDeniedException if the current user is not authorized to cancel the order
     */
    @Override
    @Transactional
    public void cancelOrder(Integer orderId) {
        if (!isAccessToOrderAllowed(orderId)) {
            throw new AccessDeniedException("Access denied");
        }
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found with ID: " + orderId));

        if (!order.getStatus().equals(Order.Status.PENDING_PAYMENT)) {
            throw new BadRequestException("You can't CANCEL order for an order that is not in PENDING_PAYMENT status");
        }
        order.setStatus(Order.Status.CANCELLED);
        orderRepository.save(order);
    }

    /**
     * Confirms the payment for a given order if valid and updates the order status to PAID.
     * Also, triggers the sending of a confirmation email with the order details.
     *
     * @param orderId       the unique identifier of the order to confirm payment for
     * @param paymentMethod the payment method used to confirm the payment. Must not be null or blank
     * @return an OrderResponseDto containing the details of the updated order
     * @throws NotFoundException     if the order with the specified ID is not found
     * @throws AccessDeniedException if the current user does not have access to the specified order
     * @throws BadRequestException   if the payment method is invalid or the order status is not PENDING_PAYMENT
     * @throws MailSendingException  if an error occurs while trying to send the order confirmation email
     */
    @Override
    @Transactional
    public OrderResponseDto confirmPayment(Integer orderId, String paymentMethod) {
        User currentUser = userService.getCurrentUser();
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found with ID: " + orderId));
        if (!order.getUser().getUserId().equals(currentUser.getUserId())) {
            throw new AccessDeniedException("Access denied");
        }
        recalculateOrderPrice(order);
        if (paymentMethod == null || paymentMethod.isBlank()) {
            throw new BadRequestException("PaymentMethod cannot be null or blank");
        }
        if (!order.getStatus().equals(Order.Status.PENDING_PAYMENT)) {
            throw new BadRequestException("You can't confirm payment for an order that is not in PENDING_PAYMENT status");
        }
        order.setStatus(Order.Status.PAID);
        orderRepository.save(order);
        cartService.clearCart();

        return orderConverter.toDto(order);
    }

    /**
     * Updates the delivery details of an existing order.
     *
     * @param orderId         the unique identifier of the order to be updated
     * @param orderRequestDto an object containing the new delivery details, including delivery address,
     *                        contact phone, and delivery method
     * @return an OrderResponseDto containing the updated order information
     * @throws AccessDeniedException    if the user is not authorized to access or update the order
     * @throws IllegalArgumentException if the dto object or any required fields (delivery address or contact phone)
     *                                  are null or invalid
     * @throws RuntimeException         if the order is not found or the specified delivery method is invalid
     */
    @Override
    @Transactional
    public OrderResponseDto updateOrderDelivery(Integer orderId, OrderRequestDto orderRequestDto) {
        if (!isAccessToOrderAllowed(orderId)) {
            throw new AccessDeniedException("Access denied");
        }

        if (orderRequestDto == null) {
            throw new IllegalArgumentException("OrderRequestDto cannot be null");
        }

        if (orderRequestDto.getDeliveryAddress() == null || orderRequestDto.getDeliveryAddress().isBlank()) {
            throw new IllegalArgumentException("Delivery address cannot be null or empty");
        }

        if (orderRequestDto.getContactPhone() == null || orderRequestDto.getContactPhone().isBlank()) {
            throw new IllegalArgumentException("Contact phone cannot be null or empty");
        }

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getStatus() != Order.Status.PENDING_PAYMENT && order.getStatus() != Order.Status.PAID) {
            throw new RuntimeException("You can't update delivery details for an order that is not in PENDING_PAYMENT or PAID status");
        }

        try {
            order.setDeliveryMethod(Order.DeliveryMethod.valueOf(orderRequestDto.getDeliveryMethod().toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid delivery method: " + orderRequestDto.getDeliveryMethod());
        }
        order.setDeliveryAddress(orderRequestDto.getDeliveryAddress());
        order.setContactPhone(orderRequestDto.getContactPhone());
        orderRepository.save(order);
        return orderConverter.toDto(order);
    }

    /**
     * Retrieves the order status DTO for a given order ID. This method ensures that the
     * current user has appropriate access to the requested order and retrieves the
     * order details if it exists.
     *
     * @param orderId the ID of the order for which the status is to be retrieved
     * @return an OrderStatusResponseDto containing the status and last updated timestamp of the order
     * @throws AccessDeniedException if the user does not have access to the specified order
     * @throws NotFoundException     if the order with the given ID is not found
     */
    @Override
    @Transactional(readOnly = true)
    public OrderStatusResponseDto getOrderStatusDto(Integer orderId) {
        if (!isAccessToOrderAllowed(orderId)) {
            throw new AccessDeniedException("Access denied");
        }
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found with ID: " + orderId));
        return new OrderStatusResponseDto(order.getStatus().name(), order.getUpdatedAt());
    }

    /**
     * Checks if the current user has access to the specified order.
     *
     * @param orderId the ID of the order to be checked for access rights
     * @return true if the current user has access to the order, false otherwise
     * @throws NotFoundException   if the order with the given ID is not found
     * @throws BadRequestException if the specified order ID is null
     */
    @Transactional(readOnly = true)
    public boolean isAccessToOrderAllowed(Integer orderId) {
        if (orderId == null) {
            throw new BadRequestException("OrderId cannot be null");
        }
        User currentUser = userService.getCurrentUser();
        if (currentUser.getRole() == User.Role.ADMIN || currentUser.getRole() == User.Role.MANAGER) {
            return true;
        }
        // USER has the right to access only their orders
        return orderRepository.findById(orderId)
                .map(o -> o.getUser() != null && o.getUser().getUserId().equals(currentUser.getUserId()))
                .orElseThrow(() -> new NotFoundException("Order not found with ID: " + orderId));
    }

    /**
     * Recalculates the total price for an order based on the current product prices and discounts.
     * Updates each order item's price at the time of purchase.
     *
     * @param order the order for which the price needs to be recalculated.
     *              Must not be null, must have a status of PENDING_PAYMENT, and must include at least one order item.
     * @throws BadRequestException if the provided order is null, or if the order status is not PENDING_PAYMENT.
     * @throws NotFoundException   if the order does not contain any order items.
     */
    @Transactional
    public void recalculateOrderPrice(Order order) {
        if (order == null) {
            throw new BadRequestException("Order cannot be null");
        }
        if (order.getStatus() != Order.Status.PENDING_PAYMENT) {
            throw new BadRequestException("The price can't be recalculated for the order not in PENDING_PAYMENT status");
        }
        List<OrderItem> orderItemList = order.getOrderItems();
        if (orderItemList == null || orderItemList.isEmpty()) {
            throw new NotFoundException("Order Items cannot be null or empty");
        }
        for (OrderItem oi : orderItemList) {
            Product product = oi.getProduct();
            BigDecimal price = product.getPrice();
            BigDecimal discountPercent = product.getDiscountPrice();

            BigDecimal discountValue = price.multiply(discountPercent).divide(new BigDecimal(100),4, RoundingMode.HALF_UP);

            BigDecimal finalPrice = price.subtract(discountValue).setScale(2, RoundingMode.HALF_UP);

            oi.setPriceAtPurchase(finalPrice);
        }
        orderRepository.save(order);
    }

    /**
     * Sends an order confirmation email to the user associated with the specified order.
     * The email includes the order details and a PDF attachment of the order summary.
     * This method is executed asynchronously in a new transactional context.
     *
     * @param orderId the unique identifier of the order for which the confirmation email should be sent.
     *                The method retrieves the order details based on this ID.
     * @throws BadRequestException  if the specified order is not found in the database.
     * @throws MailSendingException if an error occurs while generating or sending the email.
     */
    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void sendOrderConfirmationEmail(Integer orderId) {
        try {
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new NotFoundException("Order not found for email sending: " + orderId));

            byte[] pdfBytes = PdfOrderGenerator.generatePdfOrder(order);
            mailUtil.sendOrderPaidEmail(order.getUser(), order, pdfBytes);

        } catch (NotFoundException e) {
            throw new BadRequestException("Order not found for email sending: " + orderId);
        } catch (Exception e) {
            throw new MailSendingException("Error sending order confirmation email: " + e.getMessage()); // TODO Resend Email
        }
    }
}
