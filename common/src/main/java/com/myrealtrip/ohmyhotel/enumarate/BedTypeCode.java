package com.myrealtrip.ohmyhotel.enumarate;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum BedTypeCode {

    BDT01("Single"),
    BDT02("Semi Double"),
    BDT03("Double"),
    BDT04("Queen"),
    BDT05("King"),
    BDT06("Extrabed"),
    BDT07("Floor Bedding"),
    BDT08("Sofa bed"),
    BDT09("Bunk Bed"),
    BDT10("Capsule Bed"),
    BDT11("Camping Deck");

    private final String description;
}
