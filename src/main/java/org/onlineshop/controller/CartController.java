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

    /**
     * Deletes all items from the current user's cart.
     *
     * @return a response entity with HTTP status 200 (OK) to indicate the cart was cleared successfully.
     */
    @DeleteMapping
    public ResponseEntity<Void> deleteCartItem() {
        cartService.clearCart();
        return ResponseEntity.ok().build();
    }

    /**
     * Transfers the current user's cart to an order.
     *
     * @return a response entity with HTTP status 200 (OK) to indicate the cart was transferred successfully.
     */
    @GetMapping("/toOrder")
    public ResponseEntity<Void> transferToOrder() {
        cartService.transferCartToOrder();
        return ResponseEntity.ok().build();
    }

    /**
     * Gets the current user's cart.
     *
     * @return a response entity with HTTP status 200 (OK) to indicate the cart was retrieved successfully.
     */
    @GetMapping
    public ResponseEntity<Void> getCart() {
        cartService.getCartFullData();
        return ResponseEntity.ok().build();
    }
}
