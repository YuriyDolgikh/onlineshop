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
import org.onlineshop.dto.order.OrderRequestDto;
import org.onlineshop.dto.order.OrderResponseDto;
import org.onlineshop.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/orders")
@Tag(name = "Order Management", description = "APIs for managing customer orders including creation, retrieval, cancellation, and payment confirmation")
public class OrderController {

    private final OrderService orderService;

    /**
     * Creates a new order based on the provided order details.
     *
     * @param orderRequestDto the details of the order to be created, including delivery address,
     *                        delivery method, contact phone, and list of items, must not be null
     * @return a response entity containing the created order details as {@link OrderResponseDto},
     *         with an HTTP status of 201 (Created)
     */
    @Operation(
            summary = "Create new order",
            description = "Creates a new order for the current user with the provided order details including delivery information and items."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Order successfully created",
                    content = @Content(schema = @Schema(implementation = OrderResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request - invalid order data"
            )
    })
    @PostMapping
    public ResponseEntity<OrderResponseDto> saveOrder(
            @Parameter(description = "Order creation data", required = true)
            @RequestBody OrderRequestDto orderRequestDto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(orderService.saveOrder(orderRequestDto));
    }

    /**
     * Retrieves an order by its ID.
     *
     * @param orderId the ID of the order to be retrieved
     * @return a response entity containing the order details as {@link OrderResponseDto},
     * with an HTTP status of 200 (OK)
     */
    @Operation(
            summary = "Get order by ID",
            description = "Retrieves a specific order by its ID. Users can only access their own orders, while ADMIN and MANAGER can access any order."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Order retrieved successfully",
                    content = @Content(schema = @Schema(implementation = OrderResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - access to order denied"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not found - order not found"
            )
    })
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponseDto> getOrderById(
            @Parameter(
                    description = "ID of the order to retrieve",
                    required = true,
                    example = "123"
            )
            @Valid @PathVariable Integer orderId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(orderService.getOrderById(orderId));
    }

    /**
     * Retrieves a list of orders associated with a specific user ID.
     *
     * @param userId the ID of the user whose order history is to be retrieved, must not be null
     * @return a response entity containing a list of {@link OrderResponseDto} objects representing the user's orders,
     *         with an HTTP status of 200 (OK)
     */
    @Operation(
            summary = "Get user order history",
            description = "Retrieves the order history for a specific user. Users can only access their own order history, while ADMIN and MANAGER can access any user's history."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User orders retrieved successfully",
                    content = @Content(schema = @Schema(implementation = OrderResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - access to user orders denied"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not found - user not found"
            )
    })
    @GetMapping("/ordersHistory/{userId}")
    public ResponseEntity<List<OrderResponseDto>> getOrdersByUser(
            @Parameter(
                    description = "ID of the user to retrieve orders for",
                    required = true,
                    example = "456"
            )
            @Valid @PathVariable Integer userId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(orderService.getOrdersByUser(userId));
    }

    /**
     * Cancels an existing order with the specified ID.
     *
     * @param orderId the ID of the order to be canceled, must not be null
     * @return a response entity with an HTTP status of 200 (OK)
     */
    @Operation(
            summary = "Cancel order",
            description = "Cancels an existing order. Users can only cancel their own orders, while ADMIN and MANAGER can cancel any order."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Order successfully cancelled"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - access to cancel order denied"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not found - order not found"
            )
    })
    @GetMapping("/cancel/{orderId}")
    public ResponseEntity<HttpStatus> cancelOrder(
            @Parameter(
                    description = "ID of the order to cancel",
                    required = true,
                    example = "123"
            )
            @Valid @PathVariable Integer orderId) {
        orderService.cancelOrder(orderId);
        return ResponseEntity.ok().build();
    }

    /**
     * Confirms the payment for an order with the specified ID using the given payment method.
     *
     * @param orderId   the ID of the order to confirm the payment for, must not be null
     * @param payMethod the payment method to be used, must not be null
     * @return a response entity containing the updated order details as {@link OrderResponseDto},
     *         with an HTTP status of 200 (OK)
     */
    @Operation(
            summary = "Confirm order payment",
            description = "Confirms payment for an order and sends order confirmation email with PDF invoice. Users can only confirm payment for their own orders."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Payment successfully confirmed",
                    content = @Content(schema = @Schema(implementation = OrderResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request - invalid payment method"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - access to confirm payment denied"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not found - order not found"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error - failed to send confirmation email"
            )
    })
    @GetMapping("/confirm/{orderId}/{payMethod}")
    public ResponseEntity<OrderResponseDto> confirmOrder(
            @Parameter(
                    description = "ID of the order to confirm payment for",
                    required = true,
                    example = "123"
            )
            @Valid @PathVariable Integer orderId,
            @Parameter(
                    description = "Payment method to use",
                    required = true,
                    examples = {
                            @io.swagger.v3.oas.annotations.media.ExampleObject(
                                    name = "Credit Card",
                                    value = "CREDIT_CARD"
                            ),
                            @io.swagger.v3.oas.annotations.media.ExampleObject(
                                    name = "PayPal",
                                    value = "PAYPAL"
                            ),
                            @io.swagger.v3.oas.annotations.media.ExampleObject(
                                    name = "Bank Transfer",
                                    value = "BANK_TRANSFER"
                            )
                    }
            )
            @Valid @PathVariable String payMethod) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(orderService.confirmPayment(orderId, payMethod));
    }
}