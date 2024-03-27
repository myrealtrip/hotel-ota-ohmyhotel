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
public enum PromotionType {

    EARLY_BIRD("Early_Bird"),
    LAST_MINUTE("Last_Minute"),
    MIN_NIGHTS("Min_Nights"),
    NTH_NIGHT_FREE("Nth_Night_Free"),
    HOTEL_PACKAGE("Hotel_Package"),
    NONE("None");

    private static final Map<String, PromotionType> LABEL_TO_PROMOTION_TYPE = Arrays.stream(PromotionType.values())
        .collect(Collectors.toMap(PromotionType::getLabel, Function.identity()));

    private final String label;

    @JsonCreator
    public static PromotionType get(String label) {
        return LABEL_TO_PROMOTION_TYPE.get(label);
    }

    @JsonValue
    public String jsonSerializeValue() {
        return this.getLabel();
    }
}
