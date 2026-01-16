package org.onlineshop.service.util;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class PriceCalculator {

    /**
     * Calculates the discounted price for a given original price and discount percentage.
     *
     * @param price the original price of the item must not be null and cannot be negative
     * @param discountPercentage the discount percentage to be applied, must not be null, cannot
     *                           be negative, and must not exceed 100%
     * @return the discounted price rounded to two decimal places
     * @throws IllegalArgumentException if price or discountPercentage is null, price is negative,
     *                                  discountPercentage is negative, or discountPercentage exceeds 100%
     */
    public BigDecimal calculateDiscountedPrice(BigDecimal price, BigDecimal discountPercentage) {

        if (price == null) {
            throw new IllegalArgumentException("Price cannot be null.");
        }

        if (discountPercentage == null) {
            throw new IllegalArgumentException("Discount percentage cannot be null.");
        }

        if (price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Price cannot be negative.");
        }

        if (discountPercentage.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Percent of discount cannot be negative.");
        }

        if (discountPercentage.compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new IllegalArgumentException("Percent of discount cannot be greater than 100%.");
        }

        if (discountPercentage.compareTo(BigDecimal.ZERO) == 0) {
            return price.setScale(2, RoundingMode.HALF_UP);
        }

        if (discountPercentage.compareTo(BigDecimal.valueOf(100)) == 0) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }

        BigDecimal discountAmount = price.multiply(discountPercentage).divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP);

        return price.subtract(discountAmount).setScale(2, RoundingMode.HALF_UP);
    }
}