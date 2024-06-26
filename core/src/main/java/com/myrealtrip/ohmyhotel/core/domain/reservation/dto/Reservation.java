package com.myrealtrip.ohmyhotel.core.domain.reservation.dto;

import com.myrealtrip.ohmyhotel.enumarate.CanceledBy;
import com.myrealtrip.ohmyhotel.enumarate.OmhBookingStatus;
import com.myrealtrip.ohmyhotel.enumarate.ReservationStatus;
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
import java.time.LocalDateTime;
import java.time.LocalTime;

import static java.util.Objects.isNull;

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

    private ReservationStatus reservationStatus;

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

    private GuestDetail reservationUser;

    private GuestDetail checkInUser;

    private String specialRequest;

    private BigDecimal cancelPenaltySalePrice;

    private BigDecimal cancelPenaltyDepositPrice;

    private String bookingErrorCode;

    private String logs;

    private LocalDateTime confirmedAt;

    private LocalDateTime canceledAt;

    private CanceledBy canceledBy;

    private String cancelReason;

    private String cancelReasonType;

    private String omhCancelConfirmNo;

    private int confirmPendingRetryCount;

    public BigDecimal getCancelRefundAmount() {
        if (isNull(cancelPenaltySalePrice)) {
            return null;
        }
        return salePrice.subtract(cancelPenaltySalePrice);
    }

    public BigDecimal getMrtCommission() {
        return salePrice.subtract(depositPrice);
    }

    public BigDecimal getMrtCancelCommission() {
        if (isNull(cancelPenaltySalePrice)) {
            return null;
        }
        return cancelPenaltySalePrice.subtract(cancelPenaltyDepositPrice);
    }
}
