package com.myrealtrip.ohmyhotel.enumarate;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Set;

@Getter
@RequiredArgsConstructor
public enum ReservationStatus {

    PRECHECK_FAIL("precheck 실패"),
    PRECHECK_SUCCESS("precheck 성공. 이 뒤로 진행 가능하다"),
    RESERVE_CONFIRM_PENDING("확정 결과 보류"),
    RESERVE_CONFIRM("확정까지 완료된 상태"),
    RESERVE_CONFIRM_FAIL("확정 단계에서 거부/취소된 상태"),
    CANCEL_FAIL("취소 실패"),
    CANCEL_SUCCESS("취소 성공"),
    ;

    private final String description;

    public boolean canChangeTo(ReservationStatus toStatus) {
        return ReservationStatusTransition.valueOf(this.name()).getNextStatusSet().contains(toStatus);
    }

    @Getter
    @RequiredArgsConstructor
    private enum ReservationStatusTransition {
        PRECHECK_FAIL(Set.of(ReservationStatus.PRECHECK_SUCCESS)),
        PRECHECK_SUCCESS(Set.of(ReservationStatus.RESERVE_CONFIRM, ReservationStatus.RESERVE_CONFIRM_PENDING, ReservationStatus.RESERVE_CONFIRM_FAIL)),
        RESERVE_CONFIRM_PENDING(Set.of(ReservationStatus.RESERVE_CONFIRM_PENDING, ReservationStatus.RESERVE_CONFIRM, ReservationStatus.RESERVE_CONFIRM_FAIL)),
        RESERVE_CONFIRM(Set.of(ReservationStatus.CANCEL_SUCCESS, ReservationStatus.CANCEL_FAIL)),
        RESERVE_CONFIRM_FAIL(Set.of()),
        CANCEL_PENDING(Set.of(ReservationStatus.CANCEL_SUCCESS, ReservationStatus.CANCEL_FAIL)),
        CANCEL_FAIL(Set.of(ReservationStatus.CANCEL_SUCCESS)),
        CANCEL_SUCCESS(Set.of()),
        ;

        private final Set<ReservationStatus> nextStatusSet;
    }
}
