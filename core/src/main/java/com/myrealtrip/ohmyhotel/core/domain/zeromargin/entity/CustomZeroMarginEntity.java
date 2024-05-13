package com.myrealtrip.ohmyhotel.core.domain.zeromargin.entity;

import com.myrealtrip.ohmyhotel.core.domain.BaseEntity;
import com.myrealtrip.ohmyhotel.enumarate.SwitchValue;
import com.myrealtrip.ohmyhotel.enumarate.ZeroMarginType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;

@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "custom_zero_margin")
public class CustomZeroMarginEntity extends BaseEntity {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long customZeroMarginId;

    @Column(name = "hotel_id")
    private Long hotelId;

    @Column(name = "zero_margin_type")
    @Enumerated(value = EnumType.STRING)
    private ZeroMarginType zeroMarginType;

    @Column(name = "zero_margin_rate")
    private BigDecimal zeroMarginRate;
}
