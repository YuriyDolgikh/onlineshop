package org.onlineshop.dto.cartItem;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.onlineshop.entity.Product;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CartItemUpdateDto {
    @NotNull
    private Integer productId;

    @NotNull
    private Integer quantity;
}
