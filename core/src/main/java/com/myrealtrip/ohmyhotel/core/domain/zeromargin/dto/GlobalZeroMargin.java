package com.myrealtrip.ohmyhotel.core.domain.zeromargin.dto;

import com.myrealtrip.ohmyhotel.enumarate.SwitchValue;
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
public class GlobalZeroMargin {

    private Long globalZeroMarginId;

    private BigDecimal zeroMarginRate;

    private SwitchValue switchValue;

    private String applyEnv;

    public boolean isOn() {
        return this.switchValue == SwitchValue.ON;
    }
}
