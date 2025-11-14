package org.onlineshop.controller;

import org.junit.jupiter.api.BeforeEach;
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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.yml")
class StatisticControllerGetTopTenCancelledProductsTest {

    @MockBean
    private StatisticService statisticService;

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;


    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void getTopTenCancelledProducts() throws Exception {

        List<ProductStatisticResponseDto> mockResponse = new ArrayList<>();

        for (int i = 0; i < 15; i++) {
            mockResponse.add(ProductStatisticResponseDto.builder()
                    .productName("Product " + i)
                    .productCategory("Category")
                    .productPrice(new BigDecimal("100"))
                    .productDiscountPrice(new BigDecimal("90"))
                    .productQuantity(i+11)
                    .build());
        }

        List<ProductStatisticResponseDto> expectedTopTen = mockResponse.stream()
                .sorted((a,b) -> b.getProductQuantity() - a.getProductQuantity())
                .limit(10)
                .toList();

        when(statisticService.getTenCanceledProducts()).thenReturn(expectedTopTen);

        mockMvc.perform(get("/v1/statistics/topCanceled")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(10))
                .andExpect(jsonPath("$[0].productQuantity").value(expectedTopTen.get(0).getProductQuantity()))
                .andExpect(jsonPath("$[9].productQuantity").value(expectedTopTen.get(9).getProductQuantity()));


    }

}