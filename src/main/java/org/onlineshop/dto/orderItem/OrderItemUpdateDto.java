package org.onlineshop.dto.orderItem;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class OrderItemUpdateDto {

    @NotNull
    @Min(value = 1, message = "Order item ID must be at least 1")
    private Integer orderItemId;

    @NotNull
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;
}
