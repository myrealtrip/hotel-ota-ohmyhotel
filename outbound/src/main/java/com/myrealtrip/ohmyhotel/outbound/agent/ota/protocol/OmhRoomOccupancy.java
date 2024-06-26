package com.myrealtrip.ohmyhotel.outbound.agent.ota.protocol;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OmhRoomOccupancy {

    private Integer minAdultCount;

    private Integer minChildCount;

    private Integer minSumCount;

    private Integer maxAdultCount;

    private Integer maxChildCount;

    private Integer maxSumCount;
}
