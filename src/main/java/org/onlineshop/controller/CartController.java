package org.onlineshop.controller;

import lombok.RequiredArgsConstructor;
import org.onlineshop.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/carts")
public class CartController {

    private final CartService cartService;

    @DeleteMapping
    public ResponseEntity<Void> deleteCartItem() {

        cartService.clearCart();

        return ResponseEntity.ok().build();
    }

    @GetMapping("/toOrder")
    public ResponseEntity<Void> transferToOrder() {

        cartService.transferToOrder();

        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<Void> getCart() {
        cartService.getCartFullData();

        return ResponseEntity.ok().build();
    }
}
