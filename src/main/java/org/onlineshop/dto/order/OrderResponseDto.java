package org.onlineshop.dto.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.onlineshop.dto.orderItem.OrderItemResponseDto;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class OrderResponseDto {

    private Integer orderId;

    private Integer userId;

    private LocalDateTime createdAt;

    private String deliveryAddress;

    private String contactPhone;

    private String deliveryMethod;

    private String status;

    private LocalDateTime updatedAt;

    private List<OrderItemResponseDto> items;

    private Double totalPrice;

}
