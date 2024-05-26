package com.myrealtrip.ohmyhotel.core.domain.reservation.dto;

import com.myrealtrip.ohmyhotel.enumarate.RateType;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.avilability.protocol.response.OmhBedGroup;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.protocol.OmhCancelPolicy;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.protocol.OmhNightlyAmount;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
public class AdditionalOrderInfo {

    private RateType rateType;
    private String roomToken;
    private OmhCancelPolicy cancelPolicy;
    private List<OmhBedGroup> bedGroups;
    private String mealBasisCode;
    private List<OmhNightlyAmount> nightlyAmounts;
}
