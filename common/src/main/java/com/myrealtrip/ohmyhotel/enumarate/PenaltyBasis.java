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
public enum PenaltyBasis {

    FIRST_NIGHT("first_night"),
    WHOLE_NIGHTS("whole_nights");

    private static final Map<String, PenaltyBasis> LABEL_TO_PENALTY_BASIS = Arrays.stream(PenaltyBasis.values())
        .collect(Collectors.toMap(PenaltyBasis::getLabel, Function.identity()));

    private final String label;

    @JsonCreator
    public static PenaltyBasis get(String label) {
        return LABEL_TO_PENALTY_BASIS.get(label);
    }

    @JsonValue
    public String jsonSerializeValue() {
        return this.getLabel();
    }
}
