package com.myrealtrip.ohmyhotel.core.service.reservation;

import com.myrealtrip.ohmyhotel.enumarate.ReservationStatus;
import com.myrealtrip.unionstay.common.constant.booking.BookingStatus;
import com.myrealtrip.unionstay.common.constant.booking.RoomBookingStatus;
import org.springframework.stereotype.Component;

@Component
public class BookingStatusConverter {

    public BookingStatus toBookingStatus(ReservationStatus reservationStatus) {
        if (reservationStatus == ReservationStatus.RESERVE_CONFIRM) {
            return BookingStatus.CONFIRM_SUCCESS;
        }
        if (reservationStatus == ReservationStatus.RESERVE_CONFIRM_FAIL) {
            return BookingStatus.CONFIRM_FAILED_MRT;
        }
        if (reservationStatus == ReservationStatus.RESERVE_CONFIRM_PENDING) {
            return BookingStatus.CONFIRM_ACCEPTED;
        }
        if (reservationStatus == ReservationStatus.CANCEL_SUCCESS) {
            return BookingStatus.ALL_CANCEL_SUCCESS;
        }
        if (reservationStatus == ReservationStatus.CANCEL_FAIL) {
            return BookingStatus.ALL_CANCEL_FAILED;
        }
        throw new IllegalStateException("예약상태 매핑 실패");
    }

    public RoomBookingStatus toRoomBookingStatus(ReservationStatus reservationStatus) {
        if (reservationStatus == ReservationStatus.RESERVE_CONFIRM) {
            return RoomBookingStatus.BOOKED;
        }
        if (reservationStatus == ReservationStatus.CANCEL_SUCCESS) {
            return RoomBookingStatus.CANCEL_SUCCESS;
        }
        if (reservationStatus == ReservationStatus.CANCEL_FAIL) {
            return RoomBookingStatus.CANCEL_FAILED_MRT;
        }
        return null;
    }
}
