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
public enum RateOrAmount {

    RATE("rate"),
    AMOUNT("amount");

    private static final Map<String, RateOrAmount> LABEL_TO_RATE_OR_AMOUNT = Arrays.stream(RateOrAmount.values())
        .collect(Collectors.toMap(RateOrAmount::getLabel, Function.identity()));

    private final String label;

    @JsonCreator
    public static RateOrAmount get(String label) {
        return LABEL_TO_RATE_OR_AMOUNT.get(label);
    }

    @JsonValue
    public String jsonSerializeValue() {
        return this.getLabel();
    }
}
