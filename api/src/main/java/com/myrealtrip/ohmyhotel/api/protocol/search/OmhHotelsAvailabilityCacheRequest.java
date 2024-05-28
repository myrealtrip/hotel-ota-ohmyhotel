package com.myrealtrip.ohmyhotel.api.protocol.search;

import com.myrealtrip.ohmyhotel.outbound.agent.ota.avilability.protocol.request.OmhHotelsAvailabilityRequest;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.avilability.protocol.request.OmhRoomInfoRequest;
import com.myrealtrip.ohmyhotel.utils.Sha256Utils;
import com.myrealtrip.srtcommon.support.utils.ObjectMapperUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
public class OmhHotelsAvailabilityCacheRequest {

    private final String cacheKey;
    private final OmhHotelsAvailabilityRequest omhHotelsAvailabilityRequest;

    public OmhHotelsAvailabilityCacheRequest(OmhHotelsAvailabilityRequest omhHotelsAvailabilityRequest) {
        this.omhHotelsAvailabilityRequest = omhHotelsAvailabilityRequest;
        this.cacheKey = generateCacheKey();
    }

    private String generateCacheKey() {
        try {
            return Sha256Utils.encrypt(ObjectMapperUtils.writeAsString(omhHotelsAvailabilityRequest));
        } catch (Exception e) {
            throw new IllegalStateException("cache key generate fail");
        }
    }
}
