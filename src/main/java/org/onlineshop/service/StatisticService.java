package org.onlineshop.service;

import lombok.RequiredArgsConstructor;
import org.onlineshop.dto.statistic.GroupByPeriod;
import org.onlineshop.dto.statistic.ProductStatisticResponseDto;
import org.onlineshop.dto.statistic.ProfitStatisticRequestDto;
import org.onlineshop.dto.statistic.ProfitStatisticsResponseDto;
import org.onlineshop.entity.Order;
import org.onlineshop.entity.Product;
import org.onlineshop.exception.BadRequestException;
import org.onlineshop.repository.OrderRepository;
import org.onlineshop.service.converter.ProductConverter;
import org.onlineshop.service.interfaces.StatisticServiceInterface;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.IsoFields;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class StatisticService implements StatisticServiceInterface {

    private final OrderRepository orderRepository;
    private final ProductConverter productConverter;

    /**
     * Retrieves a list of top ten purchased products based on completed (paid) orders.
     *
     * @return a list of {@code ProductStatisticResponseDto} objects containing details of the
     *         top ten most purchased products, including their name, category, price, discount price,
     *         and total purchased quantity.
     */
    @Override
    public List<ProductStatisticResponseDto> getTopTenPurchasedProducts() {
        return getTopTenProducts(Order.Status.PAID);
    }

    /**
     * Retrieves a list of the top ten products based on the number of times they were
     * associated with canceled orders.
     *
     * @return a list of ProductStatisticResponseDto objects containing details of the top ten
     *         most canceled products, including their name, category, price, discount price,
     *         and total canceled quantity.
     */
    @Override
    public List<ProductStatisticResponseDto> getTenCanceledProducts() {
        return getTopTenProducts(Order.Status.CANCELLED);
    }

    /**
     * Retrieves a list of products that are in the "Pending Payment" status within the specified number of days.
     * This method calculates the total quantity of each product included in orders with the "Pending Payment" status
     * created after a given date, and returns a list of product statistics.
     *
     * @param days the number of days in the past from which orders should be considered. Orders created
     *             after (current date - days) and in "Pending Payment" status will be processed.
     * @return a list of {@code ProductStatisticResponseDto} objects containing details about the products
     *         in "Pending Payment" status and their respective total quantities.
     */
    @Transactional
    @Override
    public List<ProductStatisticResponseDto> getProductsInPendingPaymentStatus(Integer days) {
        LocalDateTime since = LocalDateTime.now().minusDays(days);
        Map<Product, Integer> productQuantityMap = new LinkedHashMap<>();
        orderRepository.findByStatusAndCreatedAtAfter(Order.Status.PENDING_PAYMENT, since)
                .forEach(o -> o.getOrderItems()
                        .forEach(oi -> {
                            productQuantityMap.merge(oi.getProduct(), oi.getQuantity(), Integer::sum);
                        }));
        return productConverter.fromMapToList(productQuantityMap);
    }

    /**
     * Generates profit statistics based on the provided request parameters.
     * Aggregates and calculates total profits for a specified time period and groups the
     * results based on the requested grouping unit (hour, day, week, or month).
     *
     * @param request the {@link ProfitStatisticRequestDto} containing the request parameters:
     *                <lo>
     *                  <li> - periodCount: the number of time periods to analyze</li>
     *                  <li> - periodUnit: the unit of time (e.g., DAYS, WEEKS, MONTHS, YEARS)</li>
     *                  <li> - groupBy: the grouping unit for profit calculation (e.g., HOUR, DAY, WEEK, MONTH)</li>
     *                </lo>
     * @return a {@link ProfitStatisticsResponseDto} containing:
     *         - the start date of the analysis period
     *         - the end date of the analysis period
     *         - the grouping identifier (groupBy unit)
     *         - a map of profits per period grouped by the specified unit
     *         - the total profit for the analysis period
     * @throws BadRequestException if the provided periodUnit or groupBy values are invalid
     */
    @Override
    public ProfitStatisticsResponseDto getProfitStatistics(ProfitStatisticRequestDto request) {

        Integer periodCount = request.getPeriodCount();
        String periodUnitStr = request.getPeriodUnit();
        String groupByStr = request.getGroupBy();

        ChronoUnit periodUnit;
        try {
            periodUnit = ChronoUnit.valueOf(periodUnitStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid period unit: " + periodUnitStr + ". Valid values are: DAYS, WEEKS, MONTHS, YEARS");
        }
        GroupByPeriod groupBy;
        try {
            groupBy = GroupByPeriod.valueOf(groupByStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(
                    "Invalid groupBy value: " + groupByStr + ". Valid values are: HOUR, DAY, WEEK, MONTH"
            );
        }

        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minus(periodCount, periodUnit);
        List<Order> orders = orderRepository.findByStatusAndCreatedAtAfter(Order.Status.PAID, startDate);
        Map<String, BigDecimal> groupedProfit = new LinkedHashMap<>();
        BigDecimal totalProfit = BigDecimal.ZERO;
        for (Order o : orders) {
            LocalDateTime createdAt = o.getCreatedAt();
            String key = switch (groupBy) {
                case HOUR -> createdAt.truncatedTo(ChronoUnit.HOURS).toString();
                case DAY -> createdAt.toLocalDate().toString();
                case WEEK -> createdAt.getYear() + "-W" + createdAt.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
                case MONTH -> createdAt.getYear() + "-" + createdAt.getMonthValue();
            };
            BigDecimal totalPrice = o.getOrderItems().stream()
                    .map(i -> i.getPriceAtPurchase().multiply(BigDecimal.valueOf(i.getQuantity())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            groupedProfit.merge(key, totalPrice, BigDecimal::add);
            totalProfit = totalProfit.add(totalPrice);

        }
        return ProfitStatisticsResponseDto.builder()
                .startDate(startDate)
                .endDate(endDate)
                .groupBy(groupBy)
                .profitsByPeriod(groupedProfit)
                .totalProfit(totalProfit)
                .build();
    }

    /**
     * Retrieves the top ten products based on the quantity sold from orders with the given status.
     *
     * @param orderStatus the order status used to filter the orders for calculating top products
     * @return a list of ProductStatisticResponseDto representing the top ten products and their statistics
     */
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
