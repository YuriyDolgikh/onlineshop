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
import org.onlineshop.dto.orderItem.OrderItemResponseDto;
import org.onlineshop.dto.orderItem.OrderItemUpdateDto;
import org.onlineshop.service.OrderItemService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/orderItems")
@Tag(name = "Order Items Management", description = "APIs for managing individual items within orders")
public class OrderItemController {

    private final OrderItemService orderItemService;

    /**
     * Deletes an item from an order by its ID.
     *
     * @param orderItemId the ID of the order item to be deleted
     * @return a ResponseEntity with HTTP status 200 (OK) upon successful deletion
     */
    @Operation(
            summary = "Delete item from order",
            description = "Removes a specific item from an order. Users can only delete items from their own orders."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Item successfully deleted from order"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - access to delete order item denied"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not found - order item not found"
            )
    })
    @DeleteMapping("{orderItemId}")
    public ResponseEntity<OrderItemResponseDto> deleteItemFromOrder(
            @Parameter(
                    description = "ID of the order item to delete",
                    required = true,
                    example = "789"
            )
            @Valid @PathVariable Integer orderItemId) {
        orderItemService.deleteItemFromOrder(orderItemId);
        return ResponseEntity.ok().build();
    }

    /**
     * Updates the quantity of a specific item in an order.
     *
     * @param orderItemUpdateDto an object containing the order item ID and the new quantity to be updated
     * @return a ResponseEntity containing an OrderItemResponseDto with the updated details and HTTP status 200 (OK)
     */
    @Operation(
            summary = "Update order item quantity",
            description = "Updates the quantity of a specific item in an order. Users can only update items in their own orders."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Item quantity successfully updated",
                    content = @Content(schema = @Schema(implementation = OrderItemResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request - invalid quantity"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - access to update order item denied"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Not found - order item not found"
            )
    })
    @PutMapping()
    public ResponseEntity<OrderItemResponseDto> updateItemQuantityInOrder(
            @Parameter(description = "Order item update data", required = true)
            @Valid @RequestBody OrderItemUpdateDto orderItemUpdateDto) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(orderItemService.updateItemQuantityInOrder(orderItemUpdateDto));
    }
}