package com.myrealtrip.ohmyhotel.core.domain.zeromargin.dto;

import com.myrealtrip.ohmyhotel.enumarate.ZeroMarginType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import java.math.BigDecimal;

@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
public class CustomZeroMargin {

    private Long customZeroMarginId;

    private Long hotelId;

    private ZeroMarginType zeroMarginType;

    private BigDecimal zeroMarginRate;
}
