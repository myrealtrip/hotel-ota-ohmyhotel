package com.myrealtrip.ohmyhotel.outbound.agent.ota.avilability.protocol;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class RoomGuest {

    private Integer adultCount;

    private Integer childCount;

    private List<Integer> childAges;
}
