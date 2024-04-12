package com.myrealtrip.ohmyhotel.api.protocol.settlement;

import com.myrealtrip.ohmyhotel.enumarate.SwitchValue;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder
public class GlobalZeroMarginUpsertRequest {

    private BigDecimal zeroMarginRate;
    private SwitchValue switchValue;
}
