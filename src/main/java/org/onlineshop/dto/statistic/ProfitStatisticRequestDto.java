package org.onlineshop.dto.statistic;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ProfitStatisticRequestDto {
    @NotNull(message = "period count must not be null")
    private Integer periodCount;

    @NotBlank
    @NotNull
    private String periodUnit;

    @NotBlank
    @NotNull
    private String  groupBy;
}
