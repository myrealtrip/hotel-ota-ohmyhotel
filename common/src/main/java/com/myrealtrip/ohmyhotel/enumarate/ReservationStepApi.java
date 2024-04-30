package com.myrealtrip.ohmyhotel.enumarate;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
public enum ReservationStepApi {

    ROOMS_AVAILABILITY,
    PRE_CHECK,
    CREATE_BOOKING,
    BOOKING_DETAIL_FOR_CONFIRM_CHECK,
    BOOKING_DETAIL_FOR_REFUND_PRICE,
    BOOKING_DETAIL_FOR_CANCEL_CHECK,
    CANCEL_BOOKING,

}
