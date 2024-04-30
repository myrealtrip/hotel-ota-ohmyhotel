package com.myrealtrip.ohmyhotel.enumarate;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Getter
public enum OmhBookingStatus {

    /**
     * 대기 상태로, 공급사쪽으로부터 예약결과에 대한 수신을 받지 못하였거나, 혹은 예약에 실패하여 미확정 예약으로 처리된 경우를 의미합니다.
     * 이 경우, 정상적인 예약이 되었고, 일시적인 문제라면 리트리브 등을 통해 정상 예약이 확인되면 예약 확정 상태로 상태가 변경됩니다.
     */
    PENDING("Pending"),

    CONFIRMED("Confirmed"),

    /**
     * 선택한 예약을 진행할 수 없는 상태를 의미합니다.
     * ex) 재고 소진, 선택한 요금이 변경됨.
     */
    UNAVAILABLE("Unavailable"), //

    CANCELLED("Cancelled");

    private static final Map<String, OmhBookingStatus> LABEL_TO_OMH_BOOKING_STATUS = Arrays.stream(OmhBookingStatus.values())
        .collect(Collectors.toMap(OmhBookingStatus::getLabel, Function.identity()));

    private final String label;

    @JsonCreator
    public static OmhBookingStatus get(String label) {
        return LABEL_TO_OMH_BOOKING_STATUS.get(label);
    }

    @JsonValue
    public String jsonSerializeValue() {
        return this.getLabel();
    }
}
