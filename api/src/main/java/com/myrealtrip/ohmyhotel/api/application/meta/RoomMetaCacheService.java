package com.myrealtrip.ohmyhotel.api.application.meta;

import com.myrealtrip.ohmyhotel.api.protocol.meta.OmhRoomInfoCacheRequest;
import com.myrealtrip.ohmyhotel.core.config.cache.CacheProperties.GlobalCache;
import com.myrealtrip.ohmyhotel.core.config.cache.annotation.GlobalCacheable;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.avilability.OmhRoomInfoAgent;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.avilability.protocol.response.OmhRoomInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoomMetaCacheService {

    private final OmhRoomInfoAgent omhRoomInfoAgent;

    @GlobalCacheable(cache = GlobalCache.ROOM_META, param = "#omhRoomInfoCacheRequest.cacheKey", type = OmhRoomInfoResponse.class)
    public OmhRoomInfoResponse getRoomInfo(OmhRoomInfoCacheRequest omhRoomInfoCacheRequest) {
        return omhRoomInfoAgent.getRoomInfo(omhRoomInfoCacheRequest.getOmhRoomInfoRequest());
    }
}
