package com.myrealtrip.ohmyhotel.enumarate;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public enum HotelStatus {
    ACTIVE("Active"), INACTIVE("Inactive");

    private static final Map<String, HotelStatus> LABEL_TO_HOTEL_STATUS = Arrays.stream(HotelStatus.values())
        .collect(Collectors.toMap(HotelStatus::getLabel, Function.identity()));

    private final String label;

    @JsonCreator
    public static HotelStatus get(String label) {
        return LABEL_TO_HOTEL_STATUS.get(label);
    }

    @JsonValue
    public String jsonSerializeValue() {
        return this.getLabel();
    }
}
