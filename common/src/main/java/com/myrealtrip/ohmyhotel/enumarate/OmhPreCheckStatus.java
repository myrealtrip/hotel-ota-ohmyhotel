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
public enum OmhPreCheckStatus {

    AVAILABLE("available"),
    AMOUNT_CHANGED("amount_changed"),
    SOLD_OUT("sold_out");

    private static final Map<String, OmhPreCheckStatus> LABEL_TO_PRE_CHECK_STATUS = Arrays.stream(OmhPreCheckStatus.values())
        .collect(Collectors.toMap(OmhPreCheckStatus::getLabel, Function.identity()));

    private final String label;

    @JsonCreator
    public static OmhPreCheckStatus get(String label) {
        return LABEL_TO_PRE_CHECK_STATUS.get(label);
    }

    @JsonValue
    public String jsonSerializeValue() {
        return this.getLabel();
    }
}
