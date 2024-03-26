package com.myrealtrip.ohmyhotel.outbound.agent.ota.avilability.protocol;

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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RoomOccupancy {

    private Integer minAdultCount;

    private Integer minChildCount;

    private Integer minSumCount;

    private Integer maxAdultCount;

    private Integer maxChildCount;

    private Integer maxSumCount;
}
