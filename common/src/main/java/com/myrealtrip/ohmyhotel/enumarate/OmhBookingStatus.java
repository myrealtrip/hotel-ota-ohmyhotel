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

    PENDING("Pending"),
    CONFIRMED("Confirmed"),
    UNAVAILABLE("Unavailable"),
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
