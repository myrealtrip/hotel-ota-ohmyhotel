package com.myrealtrip.ohmyhotel.core.domain.reservation.dto;

import com.myrealtrip.ohmyhotel.core.domain.ModifyInfo;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
public class Order {

    private Long orderId;

    private Long hotelId;

    private String hotelName;

    private String roomTypeCode;

    private String roomTypeName;

    private String ratePlanCode;

    private String ratePlanName;

    private LocalDate checkInDate;

    private LocalDate checkOutDate;

    private BigDecimal salePrice;

    private BigDecimal depositPrice;

    private Boolean zeroMarginApply;

    private BigDecimal zeroMarginApplyPrice;

    private BigDecimal mrtCommissionRate;

    private GuestCount guestCount;

    private AdditionalOrderInfo additionalInfo;
}
