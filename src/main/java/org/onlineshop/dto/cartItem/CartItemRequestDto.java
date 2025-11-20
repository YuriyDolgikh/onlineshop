package org.onlineshop.dto.cartItem;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CartItemRequestDto {
    @NotNull
    private Integer productId;

    @NotNull
    @Min(1) // quantity должен быть >= 1
    private Integer quantity;
}
