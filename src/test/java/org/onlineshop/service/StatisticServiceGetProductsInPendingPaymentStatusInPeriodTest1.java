//package org.onlineshop.service;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.onlineshop.dto.statistic.ProductStatisticResponseDto;
//import org.onlineshop.entity.*;
//import org.onlineshop.repository.*;
//import org.onlineshop.service.converter.ProductConverter;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.TestPropertySource;
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//import static org.mockito.Mockito.when;
//
//@SpringBootTest
//@ActiveProfiles("test")
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
//@TestPropertySource(locations = "classpath:application-test.yml")
//class StatisticServiceGetProductsInPendingPaymentStatusInPeriodTest1 {
//
//    @Autowired
//    private OrderRepository orderRepository;
//
//    @Autowired
//    private ProductConverter productConverter;
//    @Autowired
//    private CategoryRepository categoryRepository;
//    @Autowired
//    private StatisticService statisticService;
//    @Autowired
//    private OrderItemRepository orderItemRepository;
//    @Autowired
//    private ProductRepository productRepository;
//
//    @Autowired
//    private UserRepository userRepository;
//
//    private User user;
//
//    private List<Product> products;
//
//
//    @BeforeEach
//    void setUp() {
//        userRepository.deleteAll();
//        // Создаём категорию
//        Category category = Category.builder()
//                .categoryName("Category1")
//                .build();
//        category = categoryRepository.save(category);
//        user = new User();
//        user.setUsername("tluh");
//        user.setPhoneNumber("+491234567890");
//        user.setHashPassword("12345");
//        user.setEmail("tluh@example.com");
//        user.setRole(User.Role.USER);
//        user.setStatus(User.Status.CONFIRMED);
//        user = userRepository.save(user);
//
//
//        // Создаём продукты
//        products = new ArrayList<>();
//        for (int i = 0; i <= 15; i++) {
//            Product product = Product.builder()
//                    .id(i)
//                    .name("Product " + i)
//                    .price(new BigDecimal(100 + i * 10))
//                    .discountPrice(new BigDecimal(80 + i * 5))
//                    .category(category)
//                    .build();
//            productRepository.save(product);
//            products.add(product);
//        }
//
//        // Создаём заказы с PENDING_PAYMENT
//        for (int i = 0; i < 5; i++) {
//            Order order = new Order();
//            order.setDeliveryAddress("Berlin, Hauptstr. 1");
//            order.setContactPhone(user.getPhoneNumber());
//            order.setStatus(Order.Status.PENDING_PAYMENT);
//            order.setDeliveryMethod(Order.DeliveryMethod.COURIER);
////            order.setCreatedAt(LocalDateTime.now().minusDays(i));
////            order.setStatus(Order.Status.PENDING_PAYMENT);
////            order.setDeliveryAddress("Berlin, Hauptstr. 1");
////            order.setContactPhone("+491234567890");
////            order.setUpdatedAt(LocalDateTime.now());
////            order.setStatus(Order.Status.PENDING_PAYMENT);
//            order.setUser(user);
//            Order savedOrder = orderRepository.save(order);
//
//            List<OrderItem> items = new ArrayList<>();
//            for (int j = 0; j < products.size(); j++) {
//                int quantity = (i + j) % 5 + 1;
//                OrderItem orderItem = OrderItem.builder()
//                        .order(savedOrder)
//                        .product(products.get(j))
//                        .quantity(quantity)
//                        .priceAtPurchase(products.get(j).getDiscountPrice())
//                        .order(order)
//                        .build();
//                items.add(orderItem);
//                orderItemRepository.save(orderItem);
//            }
//            order.setOrderItems(items);
//            orderRepository.save(order);
//        }
//    }
//
//
//    @Test
//    void getProductsInPendingPaymentStatus() {
//        List<ProductStatisticResponseDto> result = statisticService.getProductsInPendingPaymentStatus(5);
//
//        assertEquals(5, result.size());
//
//        //проверка сорт по убыв
//        for (int i = 0; i < result.size() - 1; i++) {
//            assertTrue(result.get(i).getProductQuantity() >= result.get(i + 1).getProductQuantity());
//        }
//    }
//}