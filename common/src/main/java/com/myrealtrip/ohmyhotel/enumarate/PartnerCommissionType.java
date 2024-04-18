package com.myrealtrip.ohmyhotel.enumarate;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PartnerCommissionType {

    FLAT_RATE("판매가(정률)"),
    SUPPLY_PRICE("입금가");

    private final String description;
}
