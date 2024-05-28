package com.myrealtrip.ohmyhotel.core.domain.search.dto;

import com.myrealtrip.ohmyhotel.enumarate.MealBasisCode;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.protocol.OmhCancelPolicy;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
public class RatePlanDistinctField {

    private String ratePlanName;
    private OmhCancelPolicy cancelPolicy;
    private MealBasisCode mealBasisCode;
}
