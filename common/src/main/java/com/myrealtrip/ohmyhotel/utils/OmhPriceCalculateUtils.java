package com.myrealtrip.ohmyhotel.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class OmhPriceCalculateUtils {

    public static BigDecimal toSalePrice(BigDecimal depositPrice, BigDecimal mrtCommissionRate) {
        return depositPrice.add(toMrtCommission(depositPrice, mrtCommissionRate));
    }

    public static BigDecimal toMrtCommission(BigDecimal depositPrice, BigDecimal mrtCommissionRate) {
        return depositPrice
            .multiply(mrtCommissionRate)
            .divide(BigDecimal.valueOf(100), 0, RoundingMode.HALF_UP);
    }
}
