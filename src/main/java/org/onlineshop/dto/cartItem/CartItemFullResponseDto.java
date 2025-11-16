package org.onlineshop.dto.cartItem;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CartItemFullResponseDto {

    private Integer cartItemId;

    private String productName;

    private String categoryName;

    private Double productPrice;

    private Double productDiscountPrice;

    private Integer quantity;
}
