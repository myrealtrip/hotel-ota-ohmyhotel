package com.myrealtrip.ohmyhotel.core.domain.reservation.entity;

import com.myrealtrip.ohmyhotel.core.domain.BaseEntity;
import com.myrealtrip.ohmyhotel.core.domain.reservation.converter.JpaAdditionalOrderInfoConverter;
import com.myrealtrip.ohmyhotel.core.domain.reservation.converter.JpaGuestCountConverter;
import com.myrealtrip.ohmyhotel.core.domain.reservation.dto.AdditionalOrderInfo;
import com.myrealtrip.ohmyhotel.core.domain.reservation.dto.GuestCount;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "hotel_order")
public class OrderEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long orderId;

    @Column(name = "hotel_id")
    private Long hotelId;

    @Column(name = "hotel_name")
    private String hotelName;

    @Column(name = "room_type_code")
    private String roomTypeCode;

    @Column(name = "room_type_name")
    private String roomTypeName;

    @Column(name = "rate_plan_code")
    private String ratePlanCode;

    @Column(name = "rate_plan_name")
    private String ratePlanName;

    @Column(name = "check_in_date")
    private LocalDate checkInDate;

    @Column(name = "check_out_date")
    private LocalDate checkOutDate;

    @Column(name = "sale_price")
    private BigDecimal salePrice;

    @Column(name = "deposit_price")
    private BigDecimal depositPrice;

    @Column(name = "zero_margin_apply")
    private Boolean zeroMarginApply;

    @Column(name = "zero_margin_apply_price")
    private BigDecimal zeroMarginApplyPrice;

    @Column(name = "mrt_commission_rate")
    private BigDecimal mrtCommissionRate;

    @Column(name = "guest_count")
    @Convert(converter = JpaGuestCountConverter.class)
    private GuestCount guestCount;

    @Column(name = "additional_info")
    @Convert(converter = JpaAdditionalOrderInfoConverter.class)
    private AdditionalOrderInfo additionalInfo;
}
