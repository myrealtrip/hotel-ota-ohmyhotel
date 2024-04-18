package com.myrealtrip.ohmyhotel.core.domain.partner.entity;

import com.myrealtrip.ohmyhotel.core.domain.BaseEntity;
import com.myrealtrip.ohmyhotel.enumarate.PartnerCommissionType;
import com.myrealtrip.ohmyhotel.enumarate.SwitchValue;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;

@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "partner")
public class PartnerEntity extends BaseEntity {

    @Id
    @Column(name = "partner_id", nullable = false)
    private Long partnerId;

    @Column(name = "commission_type")
    @Enumerated(value = EnumType.STRING)
    private PartnerCommissionType commissionType;

    @Column(name = "commission_rate")
    private BigDecimal commissionRate;
}
