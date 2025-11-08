package org.onlineshop.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.onlineshop.dto.statistic.ProductStatisticResponseDto;
import org.onlineshop.dto.statistic.ProfitStatisticRequestDto;
import org.onlineshop.dto.statistic.ProfitStatisticsResponseDto;
import org.onlineshop.service.StatisticService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/statistics")
@Tag(name = "Statistics", description = "APIs for retrieving various business statistics including sales, profits, and product performance")
public class StatisticController {
    private final StatisticService statisticService;

    /**
     * Retrieves the top ten most purchased products based on sales data.
     *
     * @return a ResponseEntity containing a list of ProductStatisticResponseDto objects,
     *         each representing a product's sales statistics such as name, category,
     *         price, discount price, and quantity sold.
     */
    @Operation(
            summary = "Get top ten sold products",
            description = "Retrieves the top ten most purchased products based on sales data from paid orders."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Top ten sold products retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ProductStatisticResponseDto.class))
            )
    })
    @GetMapping("/topSold")
    public ResponseEntity<List<ProductStatisticResponseDto>> getTopTenPurchasedProducts() {
        return ResponseEntity.ok(statisticService.getTopTenPurchasedProducts());
    }

    /**
     * Retrieves the top ten most canceled products based on sales data.
     *
     * @return a ResponseEntity containing a list of ProductStatisticResponseDto objects,
     * each representing a product's sales statistics.
     */
    @Operation(
            summary = "Get top ten canceled products",
            description = "Retrieves the top ten most canceled products based on cancelled orders data."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Top ten canceled products retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ProductStatisticResponseDto.class))
            )
    })
    @GetMapping("/topCanceled")
    public ResponseEntity<List<ProductStatisticResponseDto>> getTenCanceledProducts() {
        return ResponseEntity.ok(statisticService.getTenCanceledProducts());
    }

    /**
     * Retrieves a list of products that have not been paid for in the specified number of days.
     *
     * @param days the number of days to check for pending payments for products
     * @return a ResponseEntity containing a list of ProductStatisticResponseDto objects,
     * each representing a product's sales statistics.
     */
    @Operation(
            summary = "Get products with pending payment",
            description = "Retrieves a list of products that have not been paid for in the specified number of days."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Pending payment products retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ProductStatisticResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request - invalid days parameter"
            )
    })
    @GetMapping("/pendingPayment/{days}")
    public ResponseEntity<List<ProductStatisticResponseDto>> getPendingPaymentProducts(
            @Parameter(
                    description = "Number of days to check for pending payments",
                    required = true,
                    example = "7"
            )
            @Valid @PathVariable Integer days) {
        List<ProductStatisticResponseDto> response = statisticService.getProductsInPendingPaymentStatus(days);
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves profit statistics based on the provided request parameters.
     *
     * @param request the ProfitStatisticRequestDto object containing the request details,
     *                including period count, period unit, and grouping criteria.
     * @return a ResponseEntity containing a ProfitStatisticsResponseDto object,
     *         which includes profit details such as the start and end date,
     *         grouping by period, profits by period, and total profit.
     */
    @Operation(
            summary = "Get profit statistics",
            description = "Retrieves profit statistics grouped by specified period (HOUR, DAY, WEEK, MONTH) for the given time range."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Profit statistics retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ProfitStatisticsResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request - invalid parameters in request body"
            )
    })
    @PostMapping("/profit")
    public ResponseEntity<ProfitStatisticsResponseDto> getProfitStatistics(
            @Parameter(
                    description = "Profit statistics request parameters",
                    required = true
            )
            @Valid @RequestBody ProfitStatisticRequestDto request) {
        ProfitStatisticsResponseDto response = statisticService.getProfitStatistics(request);
        return ResponseEntity.ok(response);
    }
}