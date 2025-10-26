package org.onlineshop.dto.orderItem;

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
public class OrderItemUpdateDto {

    @NotNull
    private Integer orderItemId; // конкретный элемент заказа

    @NotNull
    @Min(1)
    private Integer quantity;
}
