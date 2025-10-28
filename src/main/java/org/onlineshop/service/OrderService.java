package org.onlineshop.service;

import lombok.RequiredArgsConstructor;
import org.onlineshop.dto.order.OrderRequestDto;
import org.onlineshop.dto.order.OrderResponseDto;
import org.onlineshop.dto.orderItem.OrderItemRequestDto;
import org.onlineshop.entity.Order;
import org.onlineshop.entity.User;
import org.onlineshop.exception.BadRequestException;
import org.onlineshop.exception.MailSendingException;
import org.onlineshop.exception.NotFoundException;
import org.onlineshop.repository.OrderItemRepository;
import org.onlineshop.repository.OrderRepository;
import org.onlineshop.repository.UserRepository;
import org.onlineshop.service.converter.OrderConverter;
import org.onlineshop.service.mail.MailUtil;
import org.onlineshop.service.util.PdfOrderGenerator;
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
    private final PdfOrderGenerator pdfOrderGenerator;
    private final MailUtil mailUtil;

    @Transactional
    @Override
    public OrderResponseDto saveOrder(OrderRequestDto dto) {
        if (dto == null) {
            throw new BadRequestException("OrderRequestDto cannot be null");
        }
        User currentUser = userService.getCurrentUser();
        Order order = Order.builder()
                .user(currentUser)
                .deliveryAddress(dto.getDeliveryAddress())
                .contactPhone(dto.getContactPhone())
                .deliveryMethod(Order.DeliveryMethod.valueOf(dto.getDeliveryMethod().toUpperCase()))
                .status(Order.Status.OPEN)
                .build();

        orderRepository.save(order);

        if (dto.getItems() != null) {
            for (OrderItemRequestDto item : dto.getItems()) {
                orderItemService.addItemToOrder(item);
            }
        }

        order.setStatus(Order.Status.PENDING_PAYMENT);
        orderRepository.save(order);

        return orderConverter.fromEntity(order);
    }

    @Transactional
    @Override
    public OrderResponseDto getOrderById(Integer orderId) {
        if (orderId == null) {
            throw new BadRequestException("OrderId cannot be null");
        }

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found with ID: " + orderId));
        return orderConverter.fromEntity(order);
    }

    @Transactional
    @Override
    public List<OrderResponseDto> getOrdersByUser(Integer userId) {
        if (userId == null) {
            throw new BadRequestException("UserId cannot be null");
        }
        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with ID: " + userId));

        List<Order> orders = orderRepository.findByUser(currentUser);

        return orders.stream()
                .map(o -> orderConverter.fromEntity(o))
                .toList();
    }

    @Transactional
    @Override
    public OrderResponseDto updateOrderStatus(Integer orderId, String newStatus) {
        if (orderId == null) {
            throw new BadRequestException("OrderId cannot be null");
        }
        if (newStatus == null || newStatus.isBlank()) {
            throw new BadRequestException("newStatus cannot be null or blunk");
        }
        Order order  = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found with ID: " + orderId));

        Order.Status updatedStatus;
        try {
            updatedStatus = Order.Status.valueOf(newStatus.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid order status: " + newStatus);
        }

        order.setStatus(updatedStatus);
        orderRepository.save(order);
        return orderConverter.fromEntity(order);
    }

    @Transactional
    @Override
    public void cancelOrder(Integer orderId) {
        if (orderId == null) {
            throw new BadRequestException("OrderId cannot be null");
        }
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found with ID: " + orderId));

        order.setStatus(Order.Status.CANCELLED);
        orderRepository.save(order);
    }

    @Transactional
    @Override
    public OrderResponseDto confirmPayment(Integer orderId, String paymentMethod) {
        if (orderId == null) {
            throw new BadRequestException("OrderId cannot be null");
        }
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found with ID: " + orderId));

        order.setStatus(Order.Status.PAID);
        orderRepository.save(order);

        byte[] pdfBytes = pdfOrderGenerator.generatePdfOrder(order);

        try {
            mailUtil.sendOrderPaidEmail(order.getUser(), order, pdfBytes);
        } catch (Exception e) {
            throw new MailSendingException("Failed to send order confirmation email: " + e.getMessage());
        }
        return orderConverter.fromEntity(order);
    }
}
