package org.onlineshop.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.onlineshop.dto.statistic.ProfitStatisticRequestDto;
import org.onlineshop.dto.statistic.ProfitStatisticsResponseDto;
import org.onlineshop.exception.BadRequestException;
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
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.yml")
class StatisticControllerGetProfitStatisticsTest {

    @MockBean
    private StatisticService statisticService;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

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
    void getProfitStatisticsReturnOk() throws Exception {
        ProfitStatisticRequestDto requestDto = ProfitStatisticRequestDto.builder()
                .periodCount(10)
                .periodUnit("DAYS")
                .groupBy("DAY")
                .build();

        Map<String, BigDecimal> profitsByPeriod = new HashMap<>();
        profitsByPeriod.put("2025-11-01", new BigDecimal(100));
        profitsByPeriod.put("2025-11-02", new BigDecimal(120));
        profitsByPeriod.put("2025-11-03", new BigDecimal(130));

        ProfitStatisticsResponseDto responseDto = new ProfitStatisticsResponseDto();
        responseDto.setStartDate(LocalDateTime.parse("2025-11-01T00:00:00"));
        responseDto.setEndDate(LocalDateTime.parse("2025-11-03T23:59:59"));
        responseDto.setProfitsByPeriod(profitsByPeriod);
        responseDto.setTotalProfit(new BigDecimal("350"));

        when(statisticService.getProfitStatistics(requestDto)).thenReturn(responseDto);

        mockMvc.perform(post("/v1/statistics/profit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.startDate").value("2025-11-01T00:00:00"))
                .andExpect(jsonPath("$.endDate").value("2025-11-03T23:59:59"))
                .andExpect(jsonPath("$.profitsByPeriod['2025-11-01']").value(100))
                .andExpect(jsonPath("$.profitsByPeriod['2025-11-02']").value(120.0))
                .andExpect(jsonPath("$.profitsByPeriod['2025-11-03']").value(130.0))
                .andExpect(jsonPath("$.totalProfit").value(350));
    }


    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void getProfitStatisticsInvalidPeriodUnit() throws Exception {

        ProfitStatisticRequestDto requestDto = ProfitStatisticRequestDto.builder()
                .periodCount(10)
                .periodUnit("Da")
                .groupBy("DAY")
                .build();

        when(statisticService.getProfitStatistics(requestDto)).thenThrow(new BadRequestException("Invalid period unit: day. Valid values are: DAYS, WEEKS, MONTHS, YEARS"));
        mockMvc.perform(post("/v1/statistics/profit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Invalid period unit: day. Valid values are: DAYS, WEEKS, MONTHS, YEARS"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void getProfitStatisticsInvalidGroupBy() throws Exception {

        ProfitStatisticRequestDto requestDto = ProfitStatisticRequestDto.builder()
                .periodCount(10)
                .periodUnit("DAYS")
                .groupBy("Da")
                .build();

        when(statisticService.getProfitStatistics(requestDto)).thenThrow(new BadRequestException("Invalid groupBy value: Da. Valid values are: HOUR, DAY, WEEK, MONTH"
        ));
        mockMvc.perform(post("/v1/statistics/profit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Invalid groupBy value: Da. Valid values are: HOUR, DAY, WEEK, MONTH"));
    }
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void getProfitStatisticsInvalidPeriodCount() throws Exception {

        ProfitStatisticRequestDto requestDto = ProfitStatisticRequestDto.builder()
                .periodCount(-5)
                .periodUnit("DAYS")
                .groupBy("DAY")
                .build();

        when(statisticService.getProfitStatistics(requestDto))
                .thenThrow(new BadRequestException(
                        "Invalid periodCount: -5. periodCount must be greater than 0"
                ));

        mockMvc.perform(post("/v1/statistics/profit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Invalid periodCount: -5. periodCount must be greater than 0"));
    }

}

