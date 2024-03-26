package com.myrealtrip.ohmyhotel.enumarate;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Getter
@Slf4j
public enum RateType {

    STANDARD_RATE("standard_rate"),
    PACKAGE_RATE("package_rate");

    private static final Map<String, RateType> LABEL_TO_RATE_TYPE = Arrays.stream(RateType.values())
        .collect(Collectors.toMap(RateType::getLabel, Function.identity()));

    private final String label;

    @JsonCreator
    public static RateType get(String label) {
        return LABEL_TO_RATE_TYPE.get(label);
    }

    @JsonValue
    public String jsonSerializeValue() {
        return this.getLabel();
    }
}
