package org.onlineshop.service;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.onlineshop.entity.User;
import org.onlineshop.repository.OrderRepository;
import org.onlineshop.repository.UserRepository;
import org.onlineshop.service.converter.OrderConverter;
import org.onlineshop.service.mail.MailUtil;

abstract class OrderServiceBaseTest {

    @Mock
    protected OrderRepository orderRepository;

    @Mock
    protected UserService userService;

    @Mock
    CartService cartService;

    @Mock
    protected UserRepository userRepository;

    @Mock
    protected OrderConverter orderConverter;

    @Mock
    protected OrderItemService orderItemService;

    @Mock
    protected MailUtil mailUtil;

    @Spy
    @InjectMocks
    protected OrderService orderService;

    protected User userAdmin;
    protected User userManager;
    protected User userRegular;

    @BeforeEach
    void setUpBaseUsers() {
        userAdmin = User.builder()
                .userId(1)
                .role(User.Role.ADMIN)
                .build();

        userManager = User.builder()
                .userId(2)
                .role(User.Role.MANAGER)
                .build();

        userRegular = User.builder()
                .userId(3)
                .role(User.Role.USER)
                .build();
    }

}