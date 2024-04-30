package com.myrealtrip.ohmyhotel.utils;

import com.myrealtrip.srtcommon.support.utils.NumericUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class OmhPriceCalculateUtils {

    /**
     * 입금가를 판매가로 변환한다.
     * @param depositPrice
     * @param mrtCommissionRate
     * @return
     */
    public static BigDecimal toSalePrice(BigDecimal depositPrice, BigDecimal mrtCommissionRate) {
        if (NumericUtils.equals(depositPrice, BigDecimal.ZERO)) {
            return BigDecimal.ZERO;
        }
        return depositPrice.add(toMrtCommission(depositPrice, mrtCommissionRate));
    }

    public static BigDecimal toMrtCommission(BigDecimal depositPrice, BigDecimal mrtCommissionRate) {
        if (NumericUtils.equals(depositPrice, BigDecimal.ZERO)) {
            return BigDecimal.ZERO;
        }
        return depositPrice
            .multiply(mrtCommissionRate)
            .divide(BigDecimal.valueOf(100), 0, RoundingMode.HALF_UP);
    }
}
