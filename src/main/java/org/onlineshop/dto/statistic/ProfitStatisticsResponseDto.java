package org.onlineshop.dto.statistic;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfitStatisticsResponseDto {
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private GroupByPeriod groupBy;
    private Map<String, BigDecimal> profitsByPeriod;
    private BigDecimal totalProfit;
}
