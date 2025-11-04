package org.onlineshop.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.onlineshop.dto.statistic.ProductStatisticResponseDto;
import org.onlineshop.dto.statistic.ProfitStatisticRequestDto;
import org.onlineshop.dto.statistic.ProfitStatisticsResponseDto;
import org.onlineshop.entity.Product;
import org.onlineshop.service.StatisticService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/statistics")
public class StatisticController {
    private final StatisticService statisticService;

    @GetMapping("/topSold")
    public ResponseEntity<List<ProductStatisticResponseDto>> getTopTenPurchasedProducts() {
        return ResponseEntity.ok(statisticService.getTopTenPurchasedProducts());
    }


    @GetMapping("/topCanceled")
    public ResponseEntity<List<ProductStatisticResponseDto>> getTenCanceledProducts() {
        return ResponseEntity.ok(statisticService.getTenCanceledProducts());
    }

    @GetMapping("/pendingPayment")
    public ResponseEntity<List<ProductStatisticResponseDto>> getPendingPaymentProducts(@PathVariable Integer days) {

        List<ProductStatisticResponseDto> response= statisticService.getProductsInPendingPaymentStatus(days);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/profit")
    public ResponseEntity<ProfitStatisticsResponseDto> getProfitStatistics(@RequestBody ProfitStatisticRequestDto request) {
        ProfitStatisticsResponseDto response = statisticService.getProfitStatistics(
                request.getPeriodCount(),
                request.getPeriodUnit(),
                request.getGroupBy()
        );

        return ResponseEntity.ok(response);
    }



}
