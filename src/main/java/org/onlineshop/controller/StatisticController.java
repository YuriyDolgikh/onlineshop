package org.onlineshop.controller;

import lombok.RequiredArgsConstructor;
import org.onlineshop.dto.statistic.ProductStatisticResponseDto;
import org.onlineshop.service.StatisticService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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




}
