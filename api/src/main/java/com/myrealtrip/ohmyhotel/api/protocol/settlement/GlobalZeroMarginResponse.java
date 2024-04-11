package com.myrealtrip.ohmyhotel.api.protocol.settlement;

import com.myrealtrip.ohmyhotel.enumarate.SwitchValue;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class GlobalZeroMarginResponse {

    private BigDecimal zeroMarginRate;
    private SwitchValue switchValue;
}
