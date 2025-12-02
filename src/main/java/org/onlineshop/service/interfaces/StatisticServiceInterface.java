package org.onlineshop.service.interfaces;

import org.onlineshop.dto.statistic.ProductStatisticResponseDto;
import org.onlineshop.dto.statistic.ProfitStatisticRequestDto;
import org.onlineshop.dto.statistic.ProfitStatisticsResponseDto;

import java.util.List;

public interface StatisticServiceInterface {
    List<ProductStatisticResponseDto> getTopTenPurchasedProducts();

    List<ProductStatisticResponseDto> getTenCanceledProducts();

    List<ProductStatisticResponseDto> getProductsInPendingPaymentStatus(Integer days);

    ProfitStatisticsResponseDto getProfitStatistics(ProfitStatisticRequestDto request);
}
