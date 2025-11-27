//package org.onlineshop.service;
//
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.onlineshop.dto.order.OrderRequestDto;
//import org.onlineshop.dto.order.OrderResponseDto;
//import org.onlineshop.dto.orderItem.OrderItemRequestDto;
//import org.onlineshop.entity.Order;
//import org.onlineshop.exception.BadRequestException;
//
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class OrderServiceSaveOrderBaseTest extends OrderServiceBaseTest {
//
//    @Test
//    void saveOrder_whenDtoNull_shouldThrowBadRequestException() {
//        assertThrows(
//                BadRequestException.class,
//                () -> orderService.saveOrder(null),
//                "Expected BadRequestException when dto is null"
//        );
//    }
//
//    @Test
//    void saveOrder_whenDeliveryMethodInvalid_shouldThrowIllegalArgumentException() {
//        OrderRequestDto dto = OrderRequestDto.builder()
//                .deliveryMethod("INVALID")
//                .build();
//        assertThrows(
//                IllegalArgumentException.class,
//                () -> orderService.saveOrder(dto),
//                "Expected IllegalArgumentException for invalid delivery method"
//        );
//    }
//
//    @Test
//    void saveOrder_whenValid_shouldSaveOrderAndReturnDto() {
//        OrderRequestDto dto = OrderRequestDto.builder()
//                .deliveryMethod("COURIER")
//                .deliveryAddress("Some address")
//                .contactPhone("123456")
//                .items(List.of(
//                        OrderItemRequestDto.builder().productId(10).quantity(2).build()
//                ))
//                .build();
//
//        Order order = Order.builder()
//                .orderId(100)
//                .user(userRegular)
//                .deliveryMethod(Order.DeliveryMethod.COURIER)
//                .build();
//
//        when(userService.getCurrentUser()).thenReturn(userRegular);
//        when(orderRepository.save(any(Order.class))).thenReturn(order);
//
//        OrderResponseDto responseDto = new OrderResponseDto();
//        when(orderConverter.toDto(any(Order.class))).thenReturn(responseDto);
//
//        OrderResponseDto result = orderService.saveOrder(dto);
//
//        assertNotNull(result);
//        verify(orderItemService, times(dto.getItems().size()))
//                .addItemToOrder(any(OrderItemRequestDto.class));
//        verify(orderRepository).save(any(Order.class));
//        verify(orderConverter).toDto(any(Order.class));
//    }
//
//}