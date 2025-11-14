package org.onlineshop.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.onlineshop.dto.statistic.GroupByPeriod;
import org.onlineshop.dto.statistic.ProfitStatisticRequestDto;
import org.onlineshop.dto.statistic.ProfitStatisticsResponseDto;
import org.onlineshop.entity.*;
import org.onlineshop.exception.BadRequestException;
import org.onlineshop.repository.CategoryRepository;
import org.onlineshop.repository.OrderRepository;
import org.onlineshop.repository.ProductRepository;
import org.onlineshop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.yml")
class StatisticServiceProfitStatisticsTest {

    @Autowired
    private StatisticService statisticService;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        User user = User.builder().
                username("user1")
                .email("user@example.com")
                .hashPassword("hashpass12345")
                .phoneNumber("+123456789")
                .role(User.Role.USER).status(User.Status.CONFIRMED).build();
        userRepository.save(user);

        Category category = Category.builder().categoryName("Category1").build();
        categoryRepository.save(category);

        List<Product> products = new ArrayList<>();
        for (int i = 0; i <= 15; i++) {
            products.add(Product.builder().name("Product " + i).price(new BigDecimal(100 + i * 10)).discountPrice(new BigDecimal(80 + i * 5)).category(category).build());
        }
        List<Product> savedProducts = products.stream()
                .map(productRepository::save)
                .collect(Collectors.toList());

        List<Order> orders = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Order order = new Order();
            order.setUser(user);
            order.setContactPhone("+491234567890");
            order.setDeliveryMethod(Order.DeliveryMethod.POST);
            order.setDeliveryAddress("Berlin, DE");
            order.setStatus(i == 0 ? Order.Status.PAID : i == 1 ? Order.Status.IN_TRANSIT : Order.Status.DELIVERED);
            order.setCreatedAt(LocalDateTime.now().minusDays(3 - i));

            List<OrderItem> items = new ArrayList<>();
            for (Product p : savedProducts) {
                int quantity = (i + p.getName().length()) % 3 + 1;
                OrderItem orderItem = OrderItem.builder().product(p).quantity(quantity).priceAtPurchase(p.getDiscountPrice()).order(order).build();
                items.add(orderItem);
            }
            order.setOrderItems(items);
            orders.add(order);
        }
        orderRepository.saveAll(orders);
    }

    @AfterEach
    void tearDown() {
        orderRepository.deleteAll();
        productRepository.deleteAll();
        categoryRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void getProfitStatisticsValidGroupByAndPeriodUnitAndReturnProfit() {
        for (String groupBy : new String[]{"HOUR", "DAY", "WEEK", "MONTH"}) {
            ProfitStatisticRequestDto request = new ProfitStatisticRequestDto();
            request.setPeriodCount(5);
            request.setPeriodUnit("DAYS");
            request.setGroupBy(groupBy);

            ProfitStatisticsResponseDto result = statisticService.getProfitStatistics(request);

            assertNotNull(result);
            assertTrue(result.getTotalProfit().compareTo(BigDecimal.ZERO) > 0);
            assertEquals(groupBy, result.getGroupBy().name());
        }
    }

    @Test
    void getProfitStatisticsInvalidPeriodCountZeroOrNegative() {
        ProfitStatisticRequestDto request = new ProfitStatisticRequestDto();
        request.setPeriodCount(0);
        request.setPeriodUnit("DAYS");
        request.setGroupBy("DAY");

        assertThrows(BadRequestException.class, () -> statisticService.getProfitStatistics(request));
    }

    @Test
    void getProfitStatisticsInvalidGroupByAndPeriodUnitAndThrowBadRequest() {
        ProfitStatisticRequestDto request = new ProfitStatisticRequestDto();
        request.setPeriodCount(5);
        request.setPeriodUnit("INVALID");
        request.setGroupBy("DAY");

        assertThrows(BadRequestException.class, () -> statisticService.getProfitStatistics(request));
    }

    @Test
    void getProfitStatisticsInvalidGroupByAndThrowBadRequest() {
        ProfitStatisticRequestDto request = new ProfitStatisticRequestDto();
        request.setPeriodCount(5);
        request.setPeriodUnit("DAYS");
        request.setGroupBy("INVALID");

        assertThrows(BadRequestException.class, () -> statisticService.getProfitStatistics(request));
    }

}