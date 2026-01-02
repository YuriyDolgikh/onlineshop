package org.onlineshop.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StatisticServiceGetTopTenPurchasedProductsTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductConverter productConverter;

    @InjectMocks
    private StatisticService statisticService;

    @Test
    void getTopTenPurchasedProductsTest() {
        Category category = Category.builder()
                .categoryId(1)
                .categoryName("Category1")
                .build();

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

        when(orderRepository.findByStatus(Order.Status.PAID)).thenReturn(orders);

        when(productConverter.fromMapToList(Mockito.anyMap()))
                .thenAnswer(invocation -> {
                    Map<Product, Integer> map = invocation.getArgument(0);
                    List<ProductStatisticResponseDto> list = new ArrayList<>();
                    map.forEach((product, quantity) -> {
                        list.add(ProductStatisticResponseDto.builder()
                                .productName(product.getName())
                                .productCategory(product.getCategory().getCategoryName())
                                .productPrice(product.getPrice())
                                .productDiscountPrice(product.getDiscountPrice())
                                .productQuantity(quantity)
                                .build());
                    });
                    return list;
                });


        Map<Product, Integer> productQuantityMap = new HashMap<>();
        for (Order o : orders) {
            for (OrderItem oi : o.getOrderItems()) {
                productQuantityMap.merge(oi.getProduct(), oi.getQuantity(), Integer::sum);
            }
        }


        List<ProductStatisticResponseDto> expectedList = productQuantityMap.entrySet().stream()
                .sorted(Map.Entry.<Product, Integer>comparingByValue().reversed())
                .limit(10)
                .map(entry -> ProductStatisticResponseDto.builder()
                        .productName(entry.getKey().getName())
                        .productCategory(entry.getKey().getCategory().getCategoryName())
                        .productPrice(entry.getKey().getPrice())
                        .productDiscountPrice(entry.getKey().getDiscountPrice())
                        .productQuantity(entry.getValue())
                        .build())
                .toList();

        List<ProductStatisticResponseDto> actualList = statisticService.getTopTenPurchasedProducts();

        assertEquals(expectedList.size(), actualList.size());

        for (int i = 0; i < expectedList.size(); i++) {
            assertEquals(expectedList.get(i).getProductName(), actualList.get(i).getProductName());
            assertEquals(expectedList.get(i).getProductQuantity(), actualList.get(i).getProductQuantity());
        }

        //проверка сорт по убыв
        for (int i = 0; i < actualList.size() - 1; i++) {
            assertTrue(actualList.get(i).getProductQuantity() >= actualList.get(i + 1).getProductQuantity());
        }


    }


}