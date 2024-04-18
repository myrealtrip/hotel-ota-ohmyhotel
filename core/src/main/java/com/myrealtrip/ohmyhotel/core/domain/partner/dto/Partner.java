package com.myrealtrip.ohmyhotel.core.domain.partner.dto;

import com.myrealtrip.ohmyhotel.enumarate.PartnerCommissionType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import java.math.BigDecimal;

@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
public class Partner {

    private Long partnerId;

    private PartnerCommissionType commissionType;

    private BigDecimal commissionRate;
}
