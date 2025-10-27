package org.onlineshop.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.onlineshop.dto.category.CategoryRequestDto;
import org.onlineshop.dto.category.CategoryResponseDto;
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

    @PostMapping
    public ResponseEntity<OrderResponseDto> saveOrder(@RequestBody OrderRequestDto orderRequestDto){
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(orderService.saveOrder(orderRequestDto));
    }

    @GetMapping("/getOrderById/{orderId}")
    public ResponseEntity<OrderResponseDto> getOrderById(@Valid @PathVariable Integer orderId){
        return ResponseEntity
                .status(HttpStatus.FOUND)
                .body(orderService.getOrderById(orderId));
    }

    @GetMapping("/getOrdersByUser/{userId}")
    public ResponseEntity<List<OrderResponseDto>> getOrdersByUser(@Valid @PathVariable Integer userId) {
        return ResponseEntity
                .status(HttpStatus.FOUND)
                .body(orderService.getOrdersByUser(userId));
    }

    @PutMapping("/updateStatus")
    public ResponseEntity<OrderResponseDto> updateStatus(@Valid @RequestBody Integer orderId, @Valid @RequestBody String newStatus){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(orderService.updateOrderStatus(orderId, newStatus));
    }

    @PutMapping("/cancelOrder/{orderId}")
    public ResponseEntity<Void> cancelOrder(@Valid @PathVariable Integer orderId){

        orderService.cancelOrder(orderId);

       return ResponseEntity.noContent().build();

    }

    @PutMapping("/confirmOrder")
    public ResponseEntity<OrderResponseDto> confirmOrder(@Valid @RequestBody Integer orderId,@Valid @RequestBody String payMethod){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(orderService.confirmPayment(orderId,payMethod));
    }
}
