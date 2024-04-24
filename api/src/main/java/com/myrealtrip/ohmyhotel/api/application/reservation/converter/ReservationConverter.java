package com.myrealtrip.ohmyhotel.api.application.reservation.converter;

import com.myrealtrip.ohmyhotel.core.domain.reservation.dto.Order;
import com.myrealtrip.ohmyhotel.core.domain.reservation.dto.Reservation;
import com.myrealtrip.ohmyhotel.enumarate.ReservationStatus;
import com.myrealtrip.unionstay.common.constant.booking.PreCheckStatus;
import org.springframework.stereotype.Component;

@Component
public class ReservationConverter {

    public Reservation toReservation(Order order, Long reservationId, String mrtReservaitonNo, PreCheckStatus preCheckStatus) {
        return toReservationBuilder(order, mrtReservaitonNo, preCheckStatus)
            .reservationId(reservationId)
            .build();
    }

    public Reservation toReservation(Order order, String mrtReservationNo, PreCheckStatus preCheckStatus) {
        return toReservationBuilder(order, mrtReservationNo, preCheckStatus)
            .build();
    }

    public Reservation.ReservationBuilder toReservationBuilder(Order order, String mrtReservationNo, PreCheckStatus preCheckStatus) {
        return Reservation.builder()
            .orderId(order.getOrderId())
            .mrtReservationNo(mrtReservationNo)
            .omhBookCode(null)
            .hotelConfirmNo(null)
            .omhBookStatus(null)
            .reservationStatus(preCheckStatus == PreCheckStatus.AVAILABLE ?
                               ReservationStatus.PRECHECK_SUCCESS :
                               ReservationStatus.PRECHECK_FAIL)
            .hotelId(order.getHotelId())
            .hotelName(order.getHotelName())
            .roomTypeCode(order.getRoomTypeCode())
            .roomTypeName(order.getRoomTypeName())
            .ratePlanCode(order.getRatePlanCode())
            .ratePlanName(order.getRatePlanName())
            .checkInDate(order.getCheckInDate())
            .checkOutDate(order.getCheckOutDate())
            .salePrice(order.getSalePrice())
            .depositPrice(order.getDepositPrice())
            .zeroMarginApply(order.getZeroMarginApply())
            .zeroMarginApplyPrice(order.getZeroMarginApplyPrice())
            .mrtCommissionRate(order.getMrtCommissionRate())
            .guestCount(order.getGuestCount())
            .additionalInfo(order.getAdditionalInfo())
            .reservationUser(null)
            .checkInUser(null)
            .specialRequest(null)
            .cancelPenaltyAmount(null)
            .bookingErrorCode(null)
            .logs(null)
            .confirmedAt(null)
            .canceledAt(null)
            .canceledBy(null)
            .cancelReason(null)
            .cancelReasonType(null);
    }
}
