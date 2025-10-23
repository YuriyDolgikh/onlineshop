package org.onlineshop.dto.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.onlineshop.dto.orderItem.OrderItemRequestDto;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class OrderRequestDto {

    private List<OrderItemRequestDto> items;

    private String deliveryAddress;

    private String deliveryMethod;
}