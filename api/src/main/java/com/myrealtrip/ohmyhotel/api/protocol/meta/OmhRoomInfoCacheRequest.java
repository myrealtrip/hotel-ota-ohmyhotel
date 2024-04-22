package com.myrealtrip.ohmyhotel.api.protocol.meta;

import com.myrealtrip.ohmyhotel.outbound.agent.ota.avilability.protocol.request.OmhRoomInfoRequest;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
public class OmhRoomInfoCacheRequest {

    private final String cacheKey;
    private final OmhRoomInfoRequest omhRoomInfoRequest;

    public OmhRoomInfoCacheRequest(OmhRoomInfoRequest omhRoomInfoRequest) {
        this.omhRoomInfoRequest = omhRoomInfoRequest;
        this.cacheKey = generateCacheKey();
    }

    private String generateCacheKey() {
        return omhRoomInfoRequest.getHotelCode() + "-" + omhRoomInfoRequest.getRoomTypeCode();
    }
}
