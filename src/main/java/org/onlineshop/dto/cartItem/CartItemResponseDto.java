package org.onlineshop.dto.cartItem;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.onlineshop.entity.Product;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CartItemResponseDto {

    private Product product;

    private Integer quantity;
}
