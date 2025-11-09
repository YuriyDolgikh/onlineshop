package org.onlineshop.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.onlineshop.dto.orderItem.OrderItemResponseDto;
import org.onlineshop.dto.orderItem.OrderItemUpdateDto;
import org.onlineshop.service.OrderItemService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/orderItems")
public class OrderItemController {

    private final OrderItemService orderItemService;

    @DeleteMapping("{orderItemId}")
    public ResponseEntity<OrderItemResponseDto> deleteItemFromOrder(@Valid @PathVariable Integer orderItemId) {
        orderItemService.deleteItemFromOrder(orderItemId);
        return ResponseEntity.ok().build();
    }

    @PutMapping()
    public ResponseEntity<OrderItemResponseDto> updateItemQuantityInOrder(@Valid @RequestBody OrderItemUpdateDto dto) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(orderItemService.updateItemQuantityInOrder(dto));
    }
}
