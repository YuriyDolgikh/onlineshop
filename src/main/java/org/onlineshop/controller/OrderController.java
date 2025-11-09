package org.onlineshop.controller;

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
    @PostMapping
    public ResponseEntity<OrderResponseDto> saveOrder(@RequestBody OrderRequestDto orderRequestDto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(orderService.saveOrder(orderRequestDto));
    }


    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponseDto> getOrderById(@Valid @PathVariable Integer orderId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(orderService.getOrderById(orderId));
    }

    @GetMapping("/ordersHistory/{userId}")
    public ResponseEntity<List<OrderResponseDto>> getOrdersByUser(@Valid @PathVariable Integer userId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(orderService.getOrdersByUser(userId));
    }

    @GetMapping("/cancel/{orderId}")
    public ResponseEntity<HttpStatus> cancelOrder(@Valid @PathVariable Integer orderId) {
        orderService.cancelOrder(orderId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/confirm/{orderId}/{payMethod}")
    public ResponseEntity<OrderResponseDto> confirmOrder(@Valid @PathVariable Integer orderId, @Valid @PathVariable String payMethod) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(orderService.confirmPayment(orderId, payMethod));
    }
}
