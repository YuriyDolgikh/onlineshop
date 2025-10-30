package org.onlineshop.controller;

import lombok.RequiredArgsConstructor;
import org.onlineshop.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping
    public ResponseEntity<Void> transferToOrder() {

        cartService.clearCart();

        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<Void> getCart() {
        cartService.clearCart();

        return ResponseEntity.ok().build();
    }
}
