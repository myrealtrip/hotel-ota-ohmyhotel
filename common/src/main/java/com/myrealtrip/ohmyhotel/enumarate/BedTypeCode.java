package com.myrealtrip.ohmyhotel.enumarate;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum BedTypeCode {

    BDT01("Single", "싱글침대"),
    BDT02("Semi Double", "세미더블침대"),
    BDT03("Double", "더블침대"),
    BDT04("Queen", "퀸침대"),
    BDT05("King", "킹침대"),
    BDT06("Extrabed", "엑스트라베드"),
    BDT07("Floor Bedding", "바닥침구"),
    BDT08("Sofa bed", "소파베드"),
    BDT09("Bunk Bed", "벙크베드"),
    BDT10("Capsule Bed", "캡슐침대"),
    BDT11("Camping Deck", "캠핑 데크");

    private final String description;
    private final String exposedName;
}
