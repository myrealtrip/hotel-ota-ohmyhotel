package com.myrealtrip.ohmyhotel.api.protocol.search;


import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@EqualsAndHashCode
public class RateSearchId {

    private static final String SEPARATOR = "::";

    private final String roomTypeCode;

    private final String ratePlanCode;

    public static RateSearchId from(String rateSearchId) {
        String[] split = rateSearchId.split(SEPARATOR);
        return new RateSearchId(split[0], split[1]);
    }

    @Override
    public String toString() {
        return roomTypeCode + SEPARATOR + ratePlanCode;
    }
}
