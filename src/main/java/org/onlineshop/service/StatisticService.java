package org.onlineshop.service;

import lombok.RequiredArgsConstructor;
import org.onlineshop.dto.statistic.GroupByPeriod;
import org.onlineshop.dto.statistic.ProductStatisticResponseDto;
import org.onlineshop.dto.statistic.ProfitStatisticsResponseDto;
import org.onlineshop.entity.Order;
import org.onlineshop.entity.Product;
import org.onlineshop.repository.OrderRepository;
import org.onlineshop.service.converter.ProductConverter;
import org.onlineshop.service.interfaces.StatisticServiceInterface;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.IsoFields;
import java.util.*;

@RequiredArgsConstructor
@Service
public class StatisticService implements StatisticServiceInterface {

    private final OrderRepository orderRepository;
    private final ProductConverter productConverter;

    @Override
    public List<ProductStatisticResponseDto> getTopTenPurchasedProducts() {
        return getTopTenProducts(Order.Status.PAID);
    }

    @Override
    public List<ProductStatisticResponseDto> getTenCanceledProducts() {
        return getTopTenProducts(Order.Status.CANCELLED);
    }

    @Override
    public List<Product> productsInPendingPaymentStatus(Integer days) {
        LocalDateTime since = LocalDateTime.now().minusDays(days);

        List<Product> products = new ArrayList<>();

        orderRepository.findByStatusAndCreatedAtAfter(Order.Status.PENDING_PAYMENT, since)
                .forEach(o -> o.getOrderItems()
                        .forEach(oi -> products.add(oi.getProduct())));

        return products;
    }

    @Override
    public ProfitStatisticsResponseDto getProfitStatistics(Integer periodCount, ChronoUnit periodUnit, GroupByPeriod groupBy) {
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minus(periodCount, periodUnit);
        List<Order> orders = orderRepository.findByStatusAndCreatedAtAfter(Order.Status.PAID, startDate);
        Map<String, BigDecimal> groupedProfit = new LinkedHashMap<>();
        for (Order o : orders) {
            LocalDateTime createdAt = o.getCreatedAt();
            String key = switch (groupBy) {
                case HOUR -> createdAt.truncatedTo(ChronoUnit.HOURS).toString();
                case DAY -> createdAt.toLocalDate().toString();
                case WEEK -> createdAt.getYear() + "-W" + createdAt.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
                case MONTH -> createdAt.getYear() + "-" + createdAt.getMonthValue();
            };
            BigDecimal tottalPrice = o.getOrderItems().stream()
                    .map(i -> i.getPriceAtPurchase().multiply(BigDecimal.valueOf(i.getQuantity())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            groupedProfit.merge(key, tottalPrice, BigDecimal::add);
        }
        return null;
    }

    private List<ProductStatisticResponseDto> getTopTenProducts(Order.Status orderStatus) {
        Map<Product, Integer> productTopTenProducts = new HashMap<>();

        orderRepository.findByStatus(orderStatus)
                .forEach(order -> order.getOrderItems()
                        .forEach(i -> productTopTenProducts.merge(i.getProduct(), i.getQuantity(), Integer::sum)
                        ));

        Map<Product, Integer> result = productTopTenProducts.entrySet().stream()
                .sorted(Map.Entry.<Product, Integer>comparingByValue().reversed())
                .limit(10)
                .collect(
                        LinkedHashMap::new,
                        (map, entry) -> map.put(entry.getKey(), entry.getValue()), Map::putAll
                );
        return productConverter.fromMapToList(result);
    }
}
