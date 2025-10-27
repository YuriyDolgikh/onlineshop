package org.onlineshop.controller;

import lombok.RequiredArgsConstructor;
import org.onlineshop.service.OrderItemService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/orderItems")
public class OrderItemController {

    private final OrderItemService orderItemService;



}
