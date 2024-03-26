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
public enum PreCheckStatus {

    AVAILABLE("available"),
    AMOUNT_CHANGED("amount_changed"),
    SOLD_OUT("sold_out");

    private static final Map<String, PreCheckStatus> LABEL_TO_PRE_CHECK_STATUS = Arrays.stream(PreCheckStatus.values())
        .collect(Collectors.toMap(PreCheckStatus::getLabel, Function.identity()));

    private final String label;

    @JsonCreator
    public static PreCheckStatus get(String label) {
        return LABEL_TO_PRE_CHECK_STATUS.get(label);
    }

    @JsonValue
    public String jsonSerializeValue() {
        return this.getLabel();
    }
}
