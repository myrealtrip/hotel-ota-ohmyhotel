package com.myrealtrip.ohmyhotel.core.domain.zeromargin.entity;

import com.myrealtrip.ohmyhotel.core.domain.BaseEntity;
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
@Table(name = "global_zero_margin")
public class GlobalZeroMarginEntity extends BaseEntity {

    @Id
    @Column(name = "global_zero_margin_id", nullable = false)
    private Long globalZeroMarginId;

    @Column(name = "zero_margin_mark_up_rate")
    private BigDecimal zeroMarginMarkUpRate;

    @Column(name = "switch_value")
    @Enumerated(value = EnumType.STRING)
    private SwitchValue switchValue;

    @Column(name = "apply_env")
    private String applyEnv;
}
