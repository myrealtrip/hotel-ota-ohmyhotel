package com.myrealtrip.ohmyhotel.enumarate;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
public enum ReservationStepApi {

    ROOMS_AVAILABILITY,
    PRE_CHECK,
    CREATE_BOOKING,
    BOOKING_DETAIL,
    CANCEL_BOOKING,

}
