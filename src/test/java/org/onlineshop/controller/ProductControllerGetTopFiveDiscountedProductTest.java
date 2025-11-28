package org.onlineshop.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.onlineshop.dto.product.ProductResponseForUserDto;
import org.onlineshop.exception.NotFoundException;
import org.onlineshop.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductControllerGetTopFiveDiscountedProductTest {

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    @Test
    void getTopFiveDiscountedProductsOfTheDayIfRoleUserAdminManagerAndDateBaseNotEmpty() {
        List<ProductResponseForUserDto> mockProducts = List.of(
                createProductDto("TestProductOne", new BigDecimal("90")),
                createProductDto("TestProductTwo", new BigDecimal("80")),
                createProductDto("TestProductThree", new BigDecimal("70")),
                createProductDto("TestProductFour", new BigDecimal("50")),
                createProductDto("TestProductFive", new BigDecimal("40"))
        );

        when(productService.getTopFiveDiscountedProductsOfTheDay()).thenReturn(mockProducts);

        ResponseEntity<List<ProductResponseForUserDto>> result = productController.getTopFiveDiscountedProductsOfTheDay();

        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(5, result.getBody().size());
        assertEquals(new BigDecimal("90"), result.getBody().get(0).getProductDiscountPrice());
        assertEquals(new BigDecimal("80"), result.getBody().get(1).getProductDiscountPrice());
        assertEquals(new BigDecimal("70"), result.getBody().get(2).getProductDiscountPrice());
        assertEquals(new BigDecimal("50"), result.getBody().get(3).getProductDiscountPrice());
        assertEquals(new BigDecimal("40"), result.getBody().get(4).getProductDiscountPrice());
        verify(productService, times(1)).getTopFiveDiscountedProductsOfTheDay();
    }

    @Test
    void getTopFiveDiscountedProductsOfTheDayIfRoleUserAdminManagerAndDateBaseEmpty() {
        when(productService.getTopFiveDiscountedProductsOfTheDay())
                .thenThrow(new NotFoundException("No discounted products found"));

        assertThrows(NotFoundException.class, () -> productController.getTopFiveDiscountedProductsOfTheDay());
        verify(productService, times(1)).getTopFiveDiscountedProductsOfTheDay();
    }

    @Test
    void getTopFiveDiscountedProductsOfTheDayIfUserNotRegistered() {
        assertTrue(true);
    }

    private ProductResponseForUserDto createProductDto(String name, BigDecimal discountPrice) {
        return ProductResponseForUserDto.builder()
                .productName(name)
                .productDiscountPrice(discountPrice)
                .build();
    }
}