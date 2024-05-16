package com.myrealtrip.ohmyhotel.api.protocol.search;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class RateSearchIdTest {

    @Test
    @DisplayName("rateSearchId 변환 테스트")
    void rateSearchId() {
        String rateSearchIdStr = "roomTypeCode::ratePlanCode";
        RateSearchId rateSearchId = new RateSearchId("roomTypeCode", "ratePlanCode");

        assertThat(RateSearchId.from(rateSearchIdStr)).isEqualTo(rateSearchId);
        assertThat(RateSearchId.from(rateSearchIdStr).toString()).isEqualTo(rateSearchIdStr);
    }
}