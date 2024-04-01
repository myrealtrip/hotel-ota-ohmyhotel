package com.myrealtrip.ohmyhotel.enumarate;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReservationStatus {

    PRECHECK_FAIL("precheck 실패"),
    PRECHECK_SUCCESS("precheck 성공. 이 뒤로 진행 가능하다"),
    RESERVE_CONFIRM("확정까지 완료된 상태"),
    RESERVE_CONFIRM_FAIL("확정 단계에서 거부/취소된 상태"),
    CANCEL_FAIL("취소 실패"),
    CANCEL_SUCCESS("취소 성공"),
    ;

    private final String description;
}
