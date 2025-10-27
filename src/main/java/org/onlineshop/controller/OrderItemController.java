package org.onlineshop.controller;

import lombok.RequiredArgsConstructor;
import org.onlineshop.dto.orderItem.OrderItemRequestDto;
import org.onlineshop.dto.orderItem.OrderItemResponseDto;
import org.onlineshop.dto.orderItem.OrderItemUpdateDto;
import org.onlineshop.entity.Order;
import org.onlineshop.entity.OrderItem;
import org.onlineshop.service.OrderItemService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/orderItems")
public class OrderItemController {

    private final OrderItemService orderItemService;

    @PostMapping
    public ResponseEntity<OrderItemResponseDto> addItemToOrder(@RequestBody OrderItemRequestDto requestDto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(orderItemService.addItemToOrder(requestDto));

    }

    @DeleteMapping("{orderItemId}")
    public ResponseEntity<OrderItemResponseDto> deleteItemFromOrder(@PathVariable Integer orderItemId) {

        orderItemService.deleteItemFromOrder(orderItemId);

        return ResponseEntity.ok().build();

    }

    @PutMapping()
    public ResponseEntity<OrderItemResponseDto> updateItemQuantityInOrder(@RequestBody OrderItemUpdateDto dto) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(orderItemService.updateItemQuantityInOrder(dto));

    }

}
