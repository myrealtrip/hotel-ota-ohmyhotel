package com.myrealtrip.ohmyhotel.outbound.agent.ota.avilability.protocol.request;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class OmhRoomGuestCount {

    private Integer adultCount;

    private Integer childCount;

    private List<Integer> childAges;
}
