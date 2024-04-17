package com.myrealtrip.ohmyhotel.utils;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class OmhPriceCalculateUtilsTest {

    @Test
    void toSalePrice() {
        // given
        BigDecimal depositPrice = BigDecimal.valueOf(10000);
        BigDecimal mrtCommissionRate = BigDecimal.valueOf(20);

        // when
        BigDecimal salePrice = OmhPriceCalculateUtils.toSalePrice(depositPrice, mrtCommissionRate);

        // then
        assertThat(salePrice).isEqualByComparingTo(BigDecimal.valueOf(12000));
    }

    @Test
    void toMrtCommission() {
        // given
        BigDecimal depositPrice = BigDecimal.valueOf(10000);
        BigDecimal mrtCommissionRate = BigDecimal.valueOf(20);

        // when
        BigDecimal mrtCommission = OmhPriceCalculateUtils.toMrtCommission(depositPrice, mrtCommissionRate);

        // then
        assertThat(mrtCommission).isEqualByComparingTo(BigDecimal.valueOf(2000));
    }
}