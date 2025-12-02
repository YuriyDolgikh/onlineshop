package org.onlineshop.dto.orderItem;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class OrderItemResponseDto {

    private Integer orderItemId;

    private Integer productId;

    private Integer quantity;

    private BigDecimal priceAtPurchase;
}
