package com.myrealtrip.ohmyhotel.outbound.agent.ota.avilability.protocol.request;

import com.myrealtrip.ohmyhotel.enumarate.Language;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class OmhRoomInfoRequest {

    private Language language;

    private Long hotelCode;

    private String roomTypeCode;

    private String ratePlanCode;
}
