package org.onlineshop.service;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.onlineshop.dto.statistic.ProductStatisticResponseDto;
import org.onlineshop.entity.Category;
import org.onlineshop.entity.Order;
import org.onlineshop.entity.OrderItem;
import org.onlineshop.entity.Product;
import org.onlineshop.repository.OrderRepository;
import org.onlineshop.service.converter.ProductConverter;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class StatisticServiceGetTopTenProductsByStatusTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductConverter productConverter;

    @InjectMocks
    private StatisticService statisticService;

    @ParameterizedTest
    @EnumSource(value = Order.Status.class, names = {"PAID", "CANCELLED"})
    void getTopTenProductsTest(Order.Status status) {

        Category category = Category.builder()
                .categoryId(1)
                .categoryName("Category1")
                .build();

        //созд продукты
        List<Product> products = new ArrayList<>();
        for (int i = 0; i <= 15; i++) {
            products.add(Product.builder()
                    .id(i)
                    .name("Product " + i)
                    .price(new BigDecimal(100 + i * 10))
                    .discountPrice(new BigDecimal(80 + i * 5))
                    .category(category)
                    .build());
        }

        //созд заказы
        List<Order> orders = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Order order = new Order();
            List<OrderItem> items = new ArrayList<>();
            for (int j = 0; j < products.size(); j++) {
                int quantity = (i + j) % 5 + 1;
                OrderItem orderItem = OrderItem.builder()
                        .product(products.get(j))
                        .quantity(quantity)
                        .priceAtPurchase(products.get(j).getDiscountPrice())
                        .order(order)
                        .build();
                items.add(orderItem);
            }
            order.setOrderItems(items);
            orders.add(order);
        }

        when(orderRepository.findByStatus(status)).thenReturn(orders);

        //созд List<ProductStatisticResponseDto> из переданной карты:
        //для каждого продукта создаётся DTO с нужными полями;
        // quantity берётся из значения мапы.
        //И этот список возвращается, имитируя поведение настоящего конвертера.
        when(productConverter.fromMapToList(Mockito.anyMap())).thenAnswer(invocation -> {
            Map<Product, Integer> map = invocation.getArgument(0);
            List<ProductStatisticResponseDto> list = new ArrayList<>();
            map.forEach((product, quantity) -> {
                list.add(ProductStatisticResponseDto.builder()
                        .productName(product.getName())
                        .productCategory(product.getCategory().categoryName)
                        .productPrice(product.getPrice())
                        .productDiscountPrice(product.getDiscountPrice())
                                .productQuantity(quantity)
                        .build());
            });
            return list;
        });

        // Формируем expectedList
        List<ProductStatisticResponseDto> expectedList = orders.stream()
                .flatMap(o -> o.getOrderItems().stream())
                .collect(HashMap<Product,Integer>::new,
                        (map,item) -> map.merge(item.getProduct(), item.getQuantity(), Integer::sum),
                        Map::putAll)
                .entrySet().stream()
                .sorted(Map.Entry.<Product,Integer>comparingByValue().reversed())
                .limit(10)
                .map(e -> ProductStatisticResponseDto.builder()
                        .productName(e.getKey().getName())
                        .productCategory(e.getKey().getCategory().categoryName)
                        .productPrice(e.getKey().getPrice())
                        .productDiscountPrice(e.getKey().getDiscountPrice())
                        .productQuantity(e.getValue())
                        .build())
                .toList();


        List<ProductStatisticResponseDto> actualList = (status == Order.Status.PAID)
                ? statisticService.getTopTenPurchasedProducts()
                : statisticService.getTenCanceledProducts();

        assertEquals(expectedList.size(), actualList.size());
        for (int i = 0; i < expectedList.size(); i++) {
            assertEquals(expectedList.get(i).getProductName(), actualList.get(i).getProductName());
            assertEquals(expectedList.get(i).getProductQuantity(), actualList.get(i).getProductQuantity());
        }

        //проверяем отсортировано ли по убыванию количество продуктов в заказе
        for (int i = 0; i < actualList.size()-1; i++) {
            assertTrue(actualList.get(i).getProductQuantity() >= actualList.get(i+1).getProductQuantity());
        }




    }


}