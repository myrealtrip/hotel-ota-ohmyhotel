package com.myrealtrip.ohmyhotel.core.domain.reservation.dto;

import com.myrealtrip.ohmyhotel.enumarate.OmhBookingStatus;
import com.myrealtrip.ohmyhotel.enumarate.ReservationStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
public class Reservation {

    private Long reservationId;

    private Long orderId;

    private String mrtReservationNo;

    private String omhBookCode;

    private String hotelConfirmNo;

    private OmhBookingStatus omhBookStatus;

    private ReservationStatus reservationStatus;

    private Long hotelId;

    private String hotelName;

    private String roomTypeCode;

    private String roomTypeName;

    private String ratePlanCode;

    private String ratePlanName;

    private LocalDate checkInDate;

    private LocalTime checkInStartTime;

    private LocalDate checkOutDate;

    private LocalTime checkOutEndTime;

    private BigDecimal totalPrice;

    private Boolean zeroMarginApply;

    private BigDecimal zeroMarginApplyPrice;

    private BigDecimal mrtCommissionRate;

    private GuestCount guestCount;

    private AdditionalOrderInfo additionalInfo;

    private GuestDetail reservationUser;

    private GuestDetail checkInUser;

    private String specialRequest;

    private BigDecimal cancelPenaltyAmount;

    private String bookingErrorCode;

    private String logs;

    private LocalDateTime confirmedAt;

    private LocalDateTime canceledAt;

    private String canceledBy;

    private String cancelReason;

    private String cancelReasonType;
}
