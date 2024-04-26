package com.myrealtrip.ohmyhotel.outbound.slack.sender.reservation;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReservationSlackEvent {

    RESERVE_CONFIRM_FAIL("[오마이호텔] 예약 확정 실패", "오마이호텔 예약 확정에 실패하였습니다."),
    RESERVE_CONFIRM_PENDING("[오마이호텔] 예약 확정 Pending", "오마이호텔 예약상세 API 조회 결과 확정 보류 상태 입니다."),
    RESERVE_CONFIRM_RESPONSE_CHECK_FAIL("[오마이호텔] 예약확정 응답 확인 실패", "오마이호텔 예약 확정 응답을 확인하지 못했습니다. 원인 확인이 필요합니다. 최종 처리 결과를 알 수 없어 Pending 상태로 변경합니다."),
    OMH_BOOK_CODE_ABNORMAL("[오마이호텔] 예약 상태 이상", "오마이호텔 예약상세 API 조회 결과 비정상적인 상태를 가지고 있습니다. 확인이 필요합니다."),
    OMH_BOOK_DETAIL_API_FAIL("[오마이호텔] 예약 상세 조회 실패", "오마이호텔 에약상세 조회 API 가 실패하였습니다. 최종 처리 결과를 알 수 없어 Pending 상태로 변경합니다.")
    ;

    private final String eventName;
    private final String note;
}