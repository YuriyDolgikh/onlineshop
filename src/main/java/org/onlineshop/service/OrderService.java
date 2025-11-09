package org.onlineshop.service;

import lombok.RequiredArgsConstructor;
import org.onlineshop.dto.order.OrderRequestDto;
import org.onlineshop.dto.order.OrderResponseDto;
import org.onlineshop.dto.order.OrderStatusResponseDto;
import org.onlineshop.dto.orderItem.OrderItemRequestDto;
import org.onlineshop.entity.Order;
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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService implements OrderServiceInterface {
    private final OrderRepository orderRepository;
    private final UserService userService;
    private final UserRepository userRepository;
    private final OrderConverter orderConverter;
    private final OrderItemService orderItemService;
    private final MailUtil mailUtil;

    @Transactional
    @Override
    public OrderResponseDto saveOrder(OrderRequestDto dto) {
        if (dto == null) {
            throw new BadRequestException("OrderRequestDto cannot be null");
        }
        // Throws: IllegalArgumentException – if this enum type has no constant with the specified name
        Order.DeliveryMethod method = Order.DeliveryMethod.valueOf(dto.getDeliveryMethod().toUpperCase());
        User currentUser = userService.getCurrentUser();
        Order order = Order.builder()
                .user(currentUser)
                .deliveryAddress(dto.getDeliveryAddress())
                .contactPhone(dto.getContactPhone())
                .deliveryMethod(method)
                .status(Order.Status.PENDING_PAYMENT) // TODO - Зачем, ести тут же меняется на "Ожидает оплаты"?
                .build();
        if (dto.getItems() != null) {
            for (OrderItemRequestDto item : dto.getItems()) {
                orderItemService.addItemToOrder(item);
            }
        }
        orderRepository.save(order);
        return orderConverter.toDto(order);
    }

    @Transactional
    @Override
    public OrderResponseDto getOrderById(Integer orderId) {
        if (!isAccessToOrderAllowed(orderId)) {
            throw new AccessDeniedException("Access denied");
        }
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found with ID: " + orderId));
        return orderConverter.toDto(order);
    }

    @Transactional
    @Override
    public List<OrderResponseDto> getOrdersByUser(Integer userId) {
        if (userId == null) {
            throw new IllegalArgumentException("UserId cannot be null");
        }
        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with ID: " + userId));
        if (currentUser.getRole() == User.Role.USER && !currentUser.getUserId().equals(userId)) {
            throw new AccessDeniedException("Access denied");
        }
        List<Order> orders = orderRepository.findByUser(currentUser);
        return orderConverter.toDtos(orders);
    }

    @Transactional
    @Override
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

    @Transactional
    @Override
    public void cancelOrder(Integer orderId) {
        if (!isAccessToOrderAllowed(orderId)) {
            throw new AccessDeniedException("Access denied");
        }
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found with ID: " + orderId));

        order.setStatus(Order.Status.CANCELLED);
        orderRepository.save(order);
    }

    @Transactional
    @Override
    public OrderResponseDto confirmPayment(Integer orderId, String paymentMethod) {
        User currentUser = userService.getCurrentUser();
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found with ID: " + orderId));
        if (!order.getUser().getUserId().equals(currentUser.getUserId())) {
            throw new AccessDeniedException("Access denied");
        }
        order.setStatus(Order.Status.PAID);
        orderRepository.save(order);

        byte[] pdfBytes = PdfOrderGenerator.generatePdfOrder(order);

        try {
            mailUtil.sendOrderPaidEmail(order.getUser(), order, pdfBytes);
        } catch (Exception e) {
            throw new MailSendingException("Failed to send order confirmation email: " + e.getMessage());
        }
        return orderConverter.toDto(order);
    }

    @Transactional
    @Override
    public OrderResponseDto updateOrderDelivery(Integer orderId, OrderRequestDto dto) {
        if (!isAccessToOrderAllowed(orderId)) {
            throw new AccessDeniedException("Access denied");
        }
        if (dto == null) {
            throw new IllegalArgumentException("OrderRequestDto cannot be null");
        }
        if (dto.getDeliveryAddress() == null || dto.getDeliveryAddress().isBlank()) {
            throw new IllegalArgumentException("Delivery address cannot be null or empty");
        }

        if (dto.getContactPhone() == null || dto.getContactPhone().isBlank()) {
            throw new IllegalArgumentException("Contact phone cannot be null empty");
        }
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        try {
            order.setDeliveryMethod(Order.DeliveryMethod.valueOf(dto.getDeliveryMethod().toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid delivery method: " + dto.getDeliveryMethod());
        }
        order.setDeliveryAddress(dto.getDeliveryAddress());
        order.setContactPhone(dto.getContactPhone());
        orderRepository.save(order);
        return orderConverter.toDto(order);
    }

    @Transactional
    @Override
    public OrderStatusResponseDto getOrderStatusDto(Integer orderId) {
        if (!isAccessToOrderAllowed(orderId)) {
            throw new AccessDeniedException("Access denied");
        }
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found with ID: " + orderId));
        return new OrderStatusResponseDto(order.getStatus().name(), order.getUpdatedAt());
    }

    public boolean isAccessToOrderAllowed(Integer orderId) {
        if (orderId == null) {
            throw new BadRequestException("OrderId cannot be null");
        }
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found with ID: " + orderId));
        User currentUser = userService.getCurrentUser();
        if (currentUser.getRole() == User.Role.ADMIN || currentUser.getRole() == User.Role.MANAGER) {
            return true;
        }
        // обычный пользователь — только свои заказы
        return orderRepository.findById(orderId)
                .map(o -> o.getUser() != null && o.getUser().getUserId().equals(currentUser.getUserId()))
                .orElseThrow(() -> new NotFoundException("Order not found with ID: " + orderId));
    }
}
