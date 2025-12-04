package org.onlineshop.dto.cart;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.onlineshop.dto.cartItem.CartItemSimpleResponseDto;

import java.math.BigDecimal;
import java.util.List;
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CartResponseDto {

    private Integer userId;

    private List<CartItemSimpleResponseDto> cartSympleItems;

    private BigDecimal totalPrice;
}
