package org.onlineshop.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.onlineshop.dto.orderItem.OrderItemResponseDto;
import org.onlineshop.dto.orderItem.OrderItemUpdateDto;
import org.onlineshop.exception.BadRequestException;
import org.onlineshop.exception.NotFoundException;
import org.onlineshop.service.OrderItemService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
@DisplayName("OrderItemController unit tests")
class OrderItemControllerTest {

    @Mock
    private OrderItemService orderItemService;

    @InjectMocks
    private OrderItemController orderItemController;

    private static final Integer ORDER_ITEM_ID = 789;

    @AfterEach
    void tearDown() {
        Mockito.reset(orderItemService);

        SecurityContextHolder.clearContext();
    }

    @Nested
    @DisplayName("deleteItemFromOrder() endpoint tests")
    class DeleteItemFromOrderTests {

        @Test
        @DisplayName("Should return 200 OK when item is successfully deleted")
        void deleteItemFromOrder_whenOk_shouldReturnOk() {
            doNothing().when(orderItemService).deleteItemFromOrder(ORDER_ITEM_ID);

            ResponseEntity<OrderItemResponseDto> response =
                    orderItemController.deleteItemFromOrder(ORDER_ITEM_ID);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNull(response.getBody());
            verify(orderItemService).deleteItemFromOrder(ORDER_ITEM_ID);
        }

        @Test
        @DisplayName("Should propagate NotFoundException when order item does not exist")
        void deleteItemFromOrder_whenNotFound_shouldPropagateNotFound() {
            doThrow(new NotFoundException("OrderItem not found"))
                    .when(orderItemService).deleteItemFromOrder(ORDER_ITEM_ID);

            NotFoundException ex = assertThrows(
                    NotFoundException.class,
                    () -> orderItemController.deleteItemFromOrder(ORDER_ITEM_ID)
            );

            assertEquals("OrderItem not found", ex.getMessage());
            verify(orderItemService).deleteItemFromOrder(ORDER_ITEM_ID);
        }

        @Test
        @DisplayName("Should propagate AccessDeniedException when user has no rights to delete item")
        void deleteItemFromOrder_whenAccessDenied_shouldPropagateAccessDenied() {
            doThrow(new AccessDeniedException("Access denied"))
                    .when(orderItemService).deleteItemFromOrder(ORDER_ITEM_ID);

            AccessDeniedException ex = assertThrows(
                    AccessDeniedException.class,
                    () -> orderItemController.deleteItemFromOrder(ORDER_ITEM_ID)
            );

            assertEquals("Access denied", ex.getMessage());
            verify(orderItemService).deleteItemFromOrder(ORDER_ITEM_ID);
        }

        @Test
        @DisplayName("Should propagate BadRequestException when request data is invalid")
        void deleteItemFromOrder_whenBadRequest_shouldPropagateBadRequest() {
            doThrow(new BadRequestException("OrderItem ID cannot be null"))
                    .when(orderItemService).deleteItemFromOrder(ORDER_ITEM_ID);

            BadRequestException ex = assertThrows(
                    BadRequestException.class,
                    () -> orderItemController.deleteItemFromOrder(ORDER_ITEM_ID)
            );

            assertEquals("OrderItem ID cannot be null", ex.getMessage());
            verify(orderItemService).deleteItemFromOrder(ORDER_ITEM_ID);
        }
    }

    @Nested
    @DisplayName("updateItemQuantityInOrder() endpoint tests")
    class UpdateItemQuantityInOrderTests {

        @Test
        @DisplayName("Should return 200 OK and updated item when quantity is successfully updated")
        void updateItemQuantityInOrder_whenOk_shouldReturnUpdatedItem() {
            OrderItemUpdateDto requestDto = OrderItemUpdateDto.builder()
                    .orderItemId(ORDER_ITEM_ID)
                    .quantity(5)
                    .build();

            OrderItemResponseDto serviceResponse = new OrderItemResponseDto();

            when(orderItemService.updateItemQuantityInOrder(requestDto))
                    .thenReturn(serviceResponse);

            ResponseEntity<OrderItemResponseDto> response =
                    orderItemController.updateItemQuantityInOrder(requestDto);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertSame(serviceResponse, response.getBody());
            verify(orderItemService).updateItemQuantityInOrder(requestDto);
        }

        @Test
        @DisplayName("Should propagate BadRequestException when request DTO is invalid")
        void updateItemQuantityInOrder_whenBadRequest_shouldPropagateBadRequest() {
            OrderItemUpdateDto requestDto = OrderItemUpdateDto.builder()
                    .orderItemId(ORDER_ITEM_ID)
                    .quantity(0)
                    .build();

            when(orderItemService.updateItemQuantityInOrder(requestDto))
                    .thenThrow(new BadRequestException("Request cannot be null or quantity invalid"));

            BadRequestException ex = assertThrows(
                    BadRequestException.class,
                    () -> orderItemController.updateItemQuantityInOrder(requestDto)
            );

            assertEquals("Request cannot be null or quantity invalid", ex.getMessage());
            verify(orderItemService).updateItemQuantityInOrder(requestDto);
        }

        @Test
        @DisplayName("Should propagate NotFoundException when order item does not exist")
        void updateItemQuantityInOrder_whenOrderItemNotFound_shouldPropagateNotFound() {
            OrderItemUpdateDto requestDto = OrderItemUpdateDto.builder()
                    .orderItemId(ORDER_ITEM_ID)
                    .quantity(3)
                    .build();

            when(orderItemService.updateItemQuantityInOrder(requestDto))
                    .thenThrow(new NotFoundException("OrderItem not found with ID: 789"));

            NotFoundException ex = assertThrows(
                    NotFoundException.class,
                    () -> orderItemController.updateItemQuantityInOrder(requestDto)
            );

            assertEquals("OrderItem not found with ID: 789", ex.getMessage());
            verify(orderItemService).updateItemQuantityInOrder(requestDto);
        }

        @Test
        @DisplayName("Should propagate AccessDeniedException when user tries to update foreign order item")
        void updateItemQuantityInOrder_whenAccessDenied_shouldPropagateAccessDenied() {
            OrderItemUpdateDto requestDto = OrderItemUpdateDto.builder()
                    .orderItemId(ORDER_ITEM_ID)
                    .quantity(4)
                    .build();

            when(orderItemService.updateItemQuantityInOrder(requestDto))
                    .thenThrow(new BadRequestException("You can't update another user's order"));

            BadRequestException ex = assertThrows(
                    BadRequestException.class,
                    () -> orderItemController.updateItemQuantityInOrder(requestDto)
            );

            assertEquals("You can't update another user's order", ex.getMessage());
            verify(orderItemService).updateItemQuantityInOrder(requestDto);
        }
    }

}