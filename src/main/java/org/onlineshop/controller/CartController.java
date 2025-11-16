package org.onlineshop.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.onlineshop.dto.cart.CartResponseDto;
import org.onlineshop.dto.cartItem.CartItemSympleResponseDto;
import org.onlineshop.service.CartService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/carts")
@Tag(name = "Cart Management", description = "APIs for managing user shopping cart operations")
public class CartController {

    private final CartService cartService;

    /**
     * Deletes all items from the current user's cart.
     *
     * @return a response entity with HTTP status 200 (OK) to indicate the cart was cleared successfully.
     */
    @Operation(
            summary = "Clear cart",
            description = "Removes all items from the current user's shopping cart."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Cart cleared successfully"
            )
    })
    @DeleteMapping
    public ResponseEntity<Void> clearCart() {
        cartService.clearCart();
        return ResponseEntity.ok().build();
    }

    /**
     * Transfers the current user's cart to an order.
     *
     * @return a response entity with HTTP status 200 (OK) to indicate the cart was transferred successfully.
     */
    @Operation(
            summary = "Transfer cart to order",
            description = "Converts the current user's cart items into a new order and clears the cart."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Cart successfully transferred to order"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request - cart is empty or user data is invalid"
            )
    })
    @GetMapping("/toOrder")
    public ResponseEntity<Void> transferToOrder() {
        cartService.transferCartToOrder();
        return ResponseEntity.ok().build();
    }

    /**
     * Retrieves the full details of the current user's cart.
     *
     * @return a response entity containing a CartResponseDto object with the full details of the cart,
     * with an HTTP status of 200 (OK).
     */
    @Operation(
            summary = "Get cart details",
            description = "Retrieves complete information about the current user's shopping cart including all items and total price."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Cart details retrieved successfully",
                    content = @Content(schema = @Schema(implementation = CartResponseDto.class))
            )
    })
    @GetMapping
    public ResponseEntity<CartResponseDto> getCartFullData() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(cartService.getCartFullData());
    }
}