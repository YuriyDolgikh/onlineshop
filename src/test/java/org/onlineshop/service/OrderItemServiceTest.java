package org.onlineshop.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.onlineshop.dto.orderItem.OrderItemRequestDto;
import org.onlineshop.dto.orderItem.OrderItemResponseDto;
import org.onlineshop.dto.orderItem.OrderItemUpdateDto;
import org.onlineshop.entity.Order;
import org.onlineshop.entity.OrderItem;
import org.onlineshop.entity.Product;
import org.onlineshop.entity.User;
import org.onlineshop.exception.BadRequestException;
import org.onlineshop.exception.NotFoundException;
import org.onlineshop.repository.OrderItemRepository;
import org.onlineshop.repository.OrderRepository;
import org.onlineshop.repository.ProductRepository;
import org.onlineshop.service.converter.OrderItemConverter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
@DisplayName("OrderItemService unit tests")
class OrderItemServiceTest {

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OrderItemConverter orderItemConverter;

    @Mock
    private UserService userService;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderItemService orderItemService;

    private User currentUser;
    private Order openOrder;
    private Product productWithDiscount;
    private Product productWithoutDiscount;

    @BeforeEach
    @DisplayName("Initialize common test data")
    void setUp() {
        currentUser = User.builder()
                .userId(10)
                .orders(new ArrayList<>())
                .build();

        openOrder = Order.builder()
                .orderId(100)
                .user(currentUser)
                .status(Order.Status.PENDING_PAYMENT)
                .orderItems(new ArrayList<>())
                .build();

        currentUser.getOrders().add(openOrder);

        productWithDiscount = Product.builder()
                .id(1)
                .price(new BigDecimal("100"))
                .discountPrice(new BigDecimal("20"))
                .build();

        productWithoutDiscount = Product.builder()
                .id(2)
                .price(new BigDecimal("100"))
                .discountPrice(BigDecimal.ZERO)
                .build();
    }

    @Nested
    @DisplayName("addItemToOrder() tests")
    class AddItemToOrderTests {

        @Test
        @DisplayName("Should throw BadRequestException when request DTO is null")
        void addItemToOrder_whenRequestNull_shouldThrowBadRequest() {
            BadRequestException ex = assertThrows(
                    BadRequestException.class,
                    () -> orderItemService.addItemToOrder(null)
            );
            assertEquals("Request cannot be null", ex.getMessage());
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when productId or quantity are null")
        void addItemToOrder_whenProductIdOrQuantityNull_shouldThrowIllegalArgument() {
            OrderItemRequestDto dto1 = OrderItemRequestDto.builder()
                    .productId(null)
                    .quantity(1)
                    .build();

            OrderItemRequestDto dto2 = OrderItemRequestDto.builder()
                    .productId(1)
                    .quantity(null)
                    .build();

            IllegalArgumentException ex1 = assertThrows(
                    IllegalArgumentException.class,
                    () -> orderItemService.addItemToOrder(dto1)
            );
            assertEquals("Params cannot be null", ex1.getMessage());

            IllegalArgumentException ex2 = assertThrows(
                    IllegalArgumentException.class,
                    () -> orderItemService.addItemToOrder(dto2)
            );
            assertEquals("Params cannot be null", ex2.getMessage());
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when quantity < 1")
        void addItemToOrder_whenQuantityLessThanOne_shouldThrowIllegalArgument() {
            OrderItemRequestDto dto = OrderItemRequestDto.builder()
                    .productId(1)
                    .quantity(0)
                    .build();

            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> orderItemService.addItemToOrder(dto)
            );
            assertEquals("Quantity cannot be less than 1", ex.getMessage());
        }

        @Test
        @DisplayName("Should throw NotFoundException when no open order exists for current user")
        void addItemToOrder_whenNoOpenedOrder_shouldThrowNotFound() {
            User userNoOrders = User.builder()
                    .userId(20)
                    .orders(new ArrayList<>())
                    .build();

            when(userService.getCurrentUser()).thenReturn(userNoOrders);

            OrderItemRequestDto dto = OrderItemRequestDto.builder()
                    .productId(1)
                    .quantity(1)
                    .build();

            NotFoundException ex = assertThrows(
                    NotFoundException.class,
                    () -> orderItemService.addItemToOrder(dto)
            );
            assertEquals("No opened order found for current user", ex.getMessage());
        }

        @Test
        @DisplayName("Should throw NotFoundException when product does not exist")
        void addItemToOrder_whenProductNotFound_shouldThrowNotFound() {
            when(userService.getCurrentUser()).thenReturn(currentUser);
            OrderItemRequestDto dto = OrderItemRequestDto.builder()
                    .productId(999)
                    .quantity(1)
                    .build();

            when(productRepository.findById(999))
                    .thenReturn(Optional.empty());

            NotFoundException ex = assertThrows(
                    NotFoundException.class,
                    () -> orderItemService.addItemToOrder(dto)
            );
            assertEquals("Product not found with ID: 999", ex.getMessage());
        }

        @Test
        @DisplayName("Should calculate discount price when product has discount")
        void addItemToOrder_whenProductHasDiscount_shouldCalculateDiscountPrice() {
            when(userService.getCurrentUser()).thenReturn(currentUser);
            when(productRepository.findById(1)).thenReturn(Optional.of(productWithDiscount));

            OrderItemRequestDto dto = OrderItemRequestDto.builder()
                    .productId(1)
                    .quantity(2)
                    .build();

            when(orderItemRepository.save(any(OrderItem.class)))
                    .thenAnswer(inv -> inv.getArgument(0));
            when(orderItemConverter.toDto(any(OrderItem.class)))
                    .thenReturn(new OrderItemResponseDto());

            OrderItemResponseDto response = orderItemService.addItemToOrder(dto);
            assertNotNull(response);

            ArgumentCaptor<OrderItem> captor = ArgumentCaptor.forClass(OrderItem.class);
            verify(orderItemRepository).save(captor.capture());
            OrderItem saved = captor.getValue();

            assertEquals(0, saved.getPriceAtPurchase().compareTo(new BigDecimal("20")));
            assertEquals(openOrder, saved.getOrder());
            assertEquals(productWithDiscount, saved.getProduct());
            assertEquals(2, saved.getQuantity());
            assertTrue(openOrder.getOrderItems().contains(saved));
        }

        @Test
        @DisplayName("Should use full price when product has no discount")
        void addItemToOrder_whenProductWithoutDiscount_shouldUsePrice() {
            when(userService.getCurrentUser()).thenReturn(currentUser);
            when(productRepository.findById(2)).thenReturn(Optional.of(productWithoutDiscount));

            OrderItemRequestDto dto = OrderItemRequestDto.builder()
                    .productId(2)
                    .quantity(3)
                    .build();

            when(orderItemRepository.save(any(OrderItem.class)))
                    .thenAnswer(inv -> inv.getArgument(0));
            when(orderItemConverter.toDto(any(OrderItem.class)))
                    .thenReturn(new OrderItemResponseDto());

            OrderItemResponseDto response = orderItemService.addItemToOrder(dto);
            assertNotNull(response);

            ArgumentCaptor<OrderItem> captor = ArgumentCaptor.forClass(OrderItem.class);
            verify(orderItemRepository).save(captor.capture());
            OrderItem saved = captor.getValue();

            assertEquals(0, saved.getPriceAtPurchase().compareTo(new BigDecimal("100")));
            assertEquals(3, saved.getQuantity());
        }
    }

    @Nested
    @DisplayName("deleteItemFromOrder() tests")
    class DeleteItemFromOrderTests {

        @Test
        @DisplayName("Should throw BadRequestException when orderItemId is null")
        void deleteItemFromOrder_whenIdNull_shouldThrowBadRequest() {
            BadRequestException ex = assertThrows(
                    BadRequestException.class,
                    () -> orderItemService.deleteItemFromOrder(null)
            );
            assertEquals("OrderItem ID cannot be null", ex.getMessage());
        }

        @Test
        @DisplayName("Should throw NotFoundException when orderItem does not exist")
        void deleteItemFromOrder_whenOrderItemNotFound_shouldThrowNotFound() {
            when(orderItemRepository.findById(999))
                    .thenReturn(Optional.empty());

            NotFoundException ex = assertThrows(
                    NotFoundException.class,
                    () -> orderItemService.deleteItemFromOrder(999)
            );
            assertEquals("OrderItem not found with ID: 999", ex.getMessage());
        }

        @Test
        @DisplayName("Should remove item from order and delete it when valid")
        void deleteItemFromOrder_whenOk_shouldRemoveFromOrderAndDelete() {
            OrderItem orderItem = OrderItem.builder()
                    .order(openOrder)
                    .build();

            openOrder.getOrderItems().add(orderItem);

            when(orderItemRepository.findById(1))
                    .thenReturn(Optional.of(orderItem));

            when(userService.getCurrentUser())
                    .thenReturn(currentUser);

            orderItemService.deleteItemFromOrder(1);

            assertFalse(openOrder.getOrderItems().contains(orderItem));
            verify(orderItemRepository).delete(orderItem);
            verify(orderRepository).save(openOrder);
        }
    }

    @Nested
    @DisplayName("updateItemQuantityInOrder() tests")
    class UpdateItemQuantityInOrderTests {

        @Test
        @DisplayName("Should throw BadRequestException when request DTO is null")
        void updateItemQuantityInOrder_whenDtoNull_shouldThrowBadRequest() {
            BadRequestException ex = assertThrows(
                    BadRequestException.class,
                    () -> orderItemService.updateItemQuantityInOrder(null)
            );
            assertEquals("Request cannot be null", ex.getMessage());
        }

        @Test
        @DisplayName("Should throw NotFoundException when orderItem does not exist")
        void updateItemQuantityInOrder_whenOrderItemNotFound_shouldThrowNotFound() {
            OrderItemUpdateDto dto = OrderItemUpdateDto.builder()
                    .orderItemId(999)
                    .quantity(2)
                    .build();

            when(orderItemRepository.findById(999))
                    .thenReturn(Optional.empty());

            NotFoundException ex = assertThrows(
                    NotFoundException.class,
                    () -> orderItemService.updateItemQuantityInOrder(dto)
            );
            assertEquals("OrderItem not found with ID: 999", ex.getMessage());
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when quantity < 1")
        void updateItemQuantityInOrder_whenQuantityLessThanOne_shouldThrowIllegalArgument() {
            OrderItem orderItem = OrderItem.builder()
                    .order(openOrder)
                    .build();

            OrderItemUpdateDto dto = OrderItemUpdateDto.builder()
                    .orderItemId(5)
                    .quantity(0)
                    .build();

            when(orderItemRepository.findById(5))
                    .thenReturn(Optional.of(orderItem));

            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> orderItemService.updateItemQuantityInOrder(dto)
            );
            assertEquals("Quantity cannot be less than 1", ex.getMessage());
        }

        @Test
        @DisplayName("Should throw BadRequestException when user tries to modify foreign order")
        void updateItemQuantityInOrder_whenUserNotOwner_shouldThrowBadRequest() {
            User otherUser = User.builder()
                    .userId(999)
                    .build();

            Order foreignOrder = Order.builder()
                    .orderId(200)
                    .user(otherUser)
                    .build();

            OrderItem orderItem = OrderItem.builder()
                    .order(foreignOrder)
                    .quantity(5)
                    .build();

            OrderItemUpdateDto dto = OrderItemUpdateDto.builder()
                    .orderItemId(7)
                    .quantity(3)
                    .build();

            when(orderItemRepository.findById(7))
                    .thenReturn(Optional.of(orderItem));
            when(userService.getCurrentUser())
                    .thenReturn(currentUser);

            BadRequestException ex = assertThrows(
                    BadRequestException.class,
                    () -> orderItemService.updateItemQuantityInOrder(dto)
            );
            assertEquals("You can't update another user's order", ex.getMessage());
        }

        @Test
        @DisplayName("Should update quantity and return DTO when user owns the order")
        void updateItemQuantityInOrder_whenOk_shouldUpdateQuantityAndReturnDto() {
            Order ownOrder = Order.builder()
                    .orderId(300)
                    .user(currentUser)
                    .status(Order.Status.PENDING_PAYMENT)
                    .build();

            OrderItem orderItem = OrderItem.builder()
                    .order(ownOrder)
                    .quantity(5)
                    .build();

            OrderItemUpdateDto dto = OrderItemUpdateDto.builder()
                    .orderItemId(10)
                    .quantity(7)
                    .build();

            when(orderItemRepository.findById(10))
                    .thenReturn(Optional.of(orderItem));
            when(userService.getCurrentUser())
                    .thenReturn(currentUser);

            OrderItemResponseDto responseDto = new OrderItemResponseDto();
            when(orderItemConverter.toDto(orderItem)).thenReturn(responseDto);

            OrderItemResponseDto result = orderItemService.updateItemQuantityInOrder(dto);

            assertSame(responseDto, result);
            assertEquals(7, orderItem.getQuantity());
            verify(orderItemRepository).save(orderItem);
            verify(orderItemConverter).toDto(orderItem);
        }
    }

}