package org.onlineshop.dto.statistic;

import lombok.Getter;

@Getter
public enum GroupByPeriod {
    HOUR("Hour"),
    DAY("Day"),
    WEEK("Week"),
    MONTH("Month");

    private final String displayName;

    GroupByPeriod(String displayName) {
        this.displayName = displayName;
    }
}
