package org.onlineshop.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.onlineshop.dto.statistic.ProductStatisticResponseDto;
import org.onlineshop.service.StatisticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.yml")
class StatisticControllerGetPendingPaymentProductsTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StatisticService statisticService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testGetPendingPaymentProducts() throws Exception {
        ProductStatisticResponseDto product1 = ProductStatisticResponseDto.builder()
                .productName("Product A")
                .productCategory("Category 1")
                .productPrice(new BigDecimal("100.00"))
                .productDiscountPrice(new BigDecimal("90.00"))
                .productQuantity(5)
                .build();

        ProductStatisticResponseDto product2 = ProductStatisticResponseDto.builder()
                .productName("Product B")
                .productCategory("Category 1")
                .productPrice(new BigDecimal("50.00"))
                .productDiscountPrice(new BigDecimal("45.00"))
                .productQuantity(3)
                .build();

        ProductStatisticResponseDto product3 = ProductStatisticResponseDto.builder()
                .productName("Product C")
                .productCategory("Category 2")
                .productPrice(new BigDecimal("80.00"))
                .productDiscountPrice(new BigDecimal("70.00"))
                .productQuantity(8)
                .build();

        ProductStatisticResponseDto product4 = ProductStatisticResponseDto.builder()
                .productName("Product D")
                .productCategory("Category 2")
                .productPrice(new BigDecimal("30.00"))
                .productDiscountPrice(new BigDecimal("25.00"))
                .productQuantity(2)
                .build();

        ProductStatisticResponseDto product5 = ProductStatisticResponseDto.builder()
                .productName("Product E")
                .productCategory("Category 3")
                .productPrice(new BigDecimal("200.00"))
                .productDiscountPrice(new BigDecimal("180.00"))
                .productQuantity(10)
                .build();

        List<ProductStatisticResponseDto> mockResponse = List.of(product1, product2, product3, product4, product5);

        // Настройка поведения мок-сервиса
        when(statisticService.getProductsInPendingPaymentStatus(7)).thenReturn(mockResponse);

        // Выполнение запроса и проверка
        mockMvc.perform(get("/v1/statistics/pendingPayment/{days}", 7)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(5))
                .andExpect(jsonPath("$[0].productName").value("Product A"))
                .andExpect(jsonPath("$[0].productCategory").value("Category 1"))
                .andExpect(jsonPath("$[0].productPrice").value(100.00))
                .andExpect(jsonPath("$[0].productDiscountPrice").value(90.00))
                .andExpect(jsonPath("$[0].productQuantity").value(5))
                .andExpect(jsonPath("$[1].productName").value("Product B"))
                .andExpect(jsonPath("$[1].productCategory").value("Category 1"))
                .andExpect(jsonPath("$[1].productPrice").value(50.00))
                .andExpect(jsonPath("$[1].productDiscountPrice").value(45.00))
                .andExpect(jsonPath("$[1].productQuantity").value(3))
                .andExpect(jsonPath("$[2].productName").value("Product C"))
                .andExpect(jsonPath("$[2].productCategory").value("Category 2"))
                .andExpect(jsonPath("$[2].productPrice").value(80.00))
                .andExpect(jsonPath("$[2].productDiscountPrice").value(70.00))
                .andExpect(jsonPath("$[2].productQuantity").value(8))
                .andExpect(jsonPath("$[3].productName").value("Product D"))
                .andExpect(jsonPath("$[3].productCategory").value("Category 2"))
                .andExpect(jsonPath("$[3].productPrice").value(30.00))
                .andExpect(jsonPath("$[3].productDiscountPrice").value(25.00))
                .andExpect(jsonPath("$[3].productQuantity").value(2))
                .andExpect(jsonPath("$[4].productName").value("Product E"))
                .andExpect(jsonPath("$[4].productCategory").value("Category 3"))
                .andExpect(jsonPath("$[4].productPrice").value(200.00))
                .andExpect(jsonPath("$[4].productDiscountPrice").value(180.00))
                .andExpect(jsonPath("$[4].productQuantity").value(10));
    }


}