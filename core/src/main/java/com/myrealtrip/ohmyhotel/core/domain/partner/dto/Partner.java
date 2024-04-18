package com.myrealtrip.ohmyhotel.core.domain.partner.dto;

import com.myrealtrip.ohmyhotel.core.domain.ModifyInfo;
import com.myrealtrip.ohmyhotel.enumarate.PartnerCommissionType;
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
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
public class Partner extends ModifyInfo {

    private Long partnerId;

    private PartnerCommissionType commissionType;

    private BigDecimal commissionRate;
}
