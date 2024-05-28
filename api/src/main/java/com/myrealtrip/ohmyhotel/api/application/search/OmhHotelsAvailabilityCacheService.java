package com.myrealtrip.ohmyhotel.api.application.search;

import com.myrealtrip.ohmyhotel.api.protocol.search.OmhHotelsAvailabilityCacheRequest;
import com.myrealtrip.ohmyhotel.core.config.cache.CacheProperties.GlobalCache;
import com.myrealtrip.ohmyhotel.core.config.cache.annotation.GlobalCacheable;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.avilability.OmhHotelsAvailabilityAgent;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.avilability.protocol.response.OmhHotelsAvailabilityResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OmhHotelsAvailabilityCacheService {

    private final OmhHotelsAvailabilityAgent omhHotelsAvailabilityAgent;

    @GlobalCacheable(cache = GlobalCache.HOTELS_AVAILABILITY, param = "#omhHotelsAvailabilityCacheRequest.cacheKey", type = OmhHotelsAvailabilityResponse.class)
    public OmhHotelsAvailabilityResponse getHotelsAvailability(OmhHotelsAvailabilityCacheRequest omhHotelsAvailabilityCacheRequest) {
        return omhHotelsAvailabilityAgent.getHotelsAvailability(omhHotelsAvailabilityCacheRequest.getOmhHotelsAvailabilityRequest());
    }
}
