package com.myrealtrip.ohmyhotel.enumarate;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum MealBasisCode {

    A("All meals"),
    B("Breakfast included"),
    C("Dinner included"),
    D("Breakfast and dinner included"),
    E("Launch included"),
    F("Breakfast, lunch and dinner included"),
    G("Gold"),
    H("Halfboard"),
    I("All Inclusive"),
    L("Breakfast and lunch included"),
    N("None"),
    S("Silver"),
    Z("N/A");

    private final String description;
}
