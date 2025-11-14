package org.onlineshop.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.onlineshop.entity.ConfirmationCode;
import org.onlineshop.entity.Order;
import org.onlineshop.entity.User;
import org.onlineshop.repository.ConfirmationCodeRepository;
import org.onlineshop.repository.OrderRepository;
import org.onlineshop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.yml")
class ChangeOrderStatusServiceTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private ChangeOrderStatusService changeOrderStatusService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    private User user;

    @Autowired
    private ConfirmationCodeRepository confirmationCodeRepository;

    @AfterEach
    void tearDown() {
        orderRepository.deleteAll();
        userRepository.deleteAll();
    }

    @BeforeEach
    void setUp() {
        User userTwoConfirmed = User.builder()
                .username("userTwo")
                .email("userTwo@example.com")
                .hashPassword("$2a$10$35bmYiB2kQKydbNporsKfeYTff58vhJc0K0mSNXzbxetiXUJWSWFe")
                .phoneNumber("+493214568513")
                .status(User.Status.CONFIRMED)
                .role(User.Role.USER)
                .build();

        user = userRepository.save(userTwoConfirmed);


        ConfirmationCode confirmationCode = ConfirmationCode.builder()
                .code("someConfirmationCode")
                .user(userTwoConfirmed)
                .expireDataTime(LocalDateTime.now().plusDays(1))
                .build();

        confirmationCodeRepository.save(confirmationCode);


        Order order = Order.builder()
                .contactPhone(userTwoConfirmed.getPhoneNumber())
                .user(userTwoConfirmed)
                .deliveryAddress("Berlin street")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .deliveryMethod(Order.DeliveryMethod.COURIER)
                .status(Order.Status.PAID)
                .orderItems(new ArrayList<>())
                .build();

        orderRepository.save(order);


    }

    @Test
    void testProcessOrderStatusIfPaidToInTransit() {
        Order order = Order.builder()
                .contactPhone(user.getPhoneNumber())
                .user(user)
                .deliveryAddress("Berlin street")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .deliveryMethod(Order.DeliveryMethod.COURIER)
                .status(Order.Status.PAID)
                .orderItems(new ArrayList<>())
                .build();

        orderRepository.save(order);

        changeOrderStatusService.processOrderStatus();

        Order orderForCheck = orderRepository.findById(order.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order Not Found"));

        assertEquals(Order.Status.IN_TRANSIT, orderForCheck.getStatus());
    }

    @Test
    void testProcessOrderStatusIfInTransitToDelivered() {
        Order orderOne = Order.builder()
                .contactPhone(user.getPhoneNumber())
                .user(user)
                .deliveryAddress("New York street")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .deliveryMethod(Order.DeliveryMethod.COURIER)
                .status(Order.Status.IN_TRANSIT)
                .orderItems(new ArrayList<>())
                .build();

        orderRepository.save(orderOne);

        changeOrderStatusService.processOrderStatus();

        Order orderForCheck = orderRepository.findById(orderOne.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order Not Found"));

        assertEquals(Order.Status.DELIVERED, orderForCheck.getStatus());
    }

}