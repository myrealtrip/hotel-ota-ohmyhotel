package com.myrealtrip.ohmyhotel.core.domain.zeromargin.dto;

import com.myrealtrip.ohmyhotel.enumarate.ZeroMarginType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

import static java.util.Objects.nonNull;

@SuperBuilder
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class ZeroMargin {

    private BigDecimal zeroMarginRate;

    private ZeroMarginType type;

    public boolean isOn() {
        return nonNull(type) && type != ZeroMarginType.NOT_APPLY;
    }

    public static ZeroMargin empty() {
        return ZeroMargin.builder()
            .type(ZeroMarginType.NOT_APPLY)
            .zeroMarginRate(null)
            .build();
    }
}
