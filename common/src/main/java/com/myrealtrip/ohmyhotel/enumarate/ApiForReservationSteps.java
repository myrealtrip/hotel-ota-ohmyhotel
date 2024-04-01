package com.myrealtrip.ohmyhotel.enumarate;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ApiForReservationSteps {

    ROOMS_AVAILABILITY("주문서 진입시 호출"), 
    ROOMS_INFORMATION("주문서 진입시 호출"), 
    PRE_CHECK("결제 진행시 호출"), 
    CREATE_BOOKING("결제 완료 후 호출"), 
    BOOKING_DETAIL("예약 확정/취소 후 호출"), 
    CANCEL_BOOKING("예약 취소시 호출"); 

    private final String description;
}
