package com.myrealtrip.ohmyhotel.enumarate;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.EnumUtils;

@RequiredArgsConstructor
@Getter
public enum MealBasisCode {

    A("All meals", "조식, 중식, 석식 포함"),
    B("Breakfast included", "조식 포함"),
    C("Dinner included", "석식 포함"),
    D("Breakfast and dinner included", "조식, 석식 포함"),
    E("Launch included", "중식 포함"),
    F("Breakfast, lunch and dinner included", "조식, 중식, 석식 포함"),
    G("Gold", "Gold"),
    H("Halfboard", "Halfboard"),
    I("All Inclusive", "올 인클루시브"),
    L("Breakfast and lunch included", "조식, 중식 포함"),
    N("None", null),
    S("Silver", "Silver"),
    Z("N/A", null),
    NONE("", null);

    private final String description;
    private final String exposedName;
}
