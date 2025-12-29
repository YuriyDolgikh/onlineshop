package org.onlineshop.dto.order;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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

    @NotBlank(message = "Delivery address must by not Blank or Null")
    @Size(min = 3, max = 200, message = "Delivery address must be between 3 and 200 characters")
    private String deliveryAddress;

    @NotBlank(message = "Delivery method must by not Blank or Null")
    private String deliveryMethod;

    @NotBlank(message = "Contact phone must by not Blank or Null")
    @Pattern(
            regexp = "^\\+?[0-9]{7,15}$",
            message = "Phone number must contain only digits and may start with +, length 7–15"
    )
    private String contactPhone;
}