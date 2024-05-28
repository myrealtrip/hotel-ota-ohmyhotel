package com.myrealtrip.ohmyhotel.core.service;

import com.myrealtrip.ohmyhotel.core.domain.search.dto.RatePlan;
import com.myrealtrip.ohmyhotel.enumarate.MealBasisCode;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.protocol.OmhCancelPolicy;
import org.apache.kafka.common.protocol.types.Field.Str;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class RatePlanDistinctServiceTest {

    private RatePlanDistinctService ratePlanDistinctService = new RatePlanDistinctService();

    @Test
    @DisplayName("중복을 제거한 ratePlanCode 를 반환한다.")
    void getExposeRatePlanCode() {
        RatePlan ratePlan1 = RatePlan.builder()
            .ratePlanCode("1")
            .ratePlanName("room only")
            .cancelPolicy(OmhCancelPolicy.builder().build())
            .mealBasisCode(MealBasisCode.A)
            .totalNetAmount(BigDecimal.valueOf(10000))
            .build();

        RatePlan ratePlan2 = RatePlan.builder()
            .ratePlanCode("2")
            .ratePlanName("room only")
            .cancelPolicy(OmhCancelPolicy.builder().build())
            .mealBasisCode(MealBasisCode.A)
            .totalNetAmount(BigDecimal.valueOf(5000))
            .build();

        RatePlan ratePlan3 = RatePlan.builder()
            .ratePlanCode("3")
            .ratePlanName("room only")
            .cancelPolicy(OmhCancelPolicy.builder().build())
            .mealBasisCode(MealBasisCode.A)
            .totalNetAmount(BigDecimal.valueOf(15000))
            .build();

        RatePlan ratePlan4 = RatePlan.builder()
            .ratePlanCode("4")
            .ratePlanName(null)
            .cancelPolicy(OmhCancelPolicy.builder().build())
            .mealBasisCode(MealBasisCode.A)
            .totalNetAmount(BigDecimal.valueOf(10000))
            .build();

        Set<String> result = new HashSet<>(ratePlanDistinctService.getExposeRatePlanCode(List.of(ratePlan1, ratePlan2, ratePlan3, ratePlan4)));

        assertThat(result).isEqualTo(Set.of("2", "4"));
    }
}