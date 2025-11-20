package org.onlineshop.service.interfaces;

import org.onlineshop.dto.statistic.GroupByPeriod;
import org.onlineshop.dto.statistic.ProductStatisticResponseDto;
import org.onlineshop.dto.statistic.ProfitStatisticRequestDto;
import org.onlineshop.dto.statistic.ProfitStatisticsResponseDto;
import org.onlineshop.entity.Product;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

public interface StatisticServiceInterface {
    List<ProductStatisticResponseDto> getTopTenPurchasedProducts();
    List<ProductStatisticResponseDto> getTenCanceledProducts();
    List<ProductStatisticResponseDto> getProductsInPendingPaymentStatus(Integer days);
    ProfitStatisticsResponseDto getProfitStatistics(ProfitStatisticRequestDto request);


}
