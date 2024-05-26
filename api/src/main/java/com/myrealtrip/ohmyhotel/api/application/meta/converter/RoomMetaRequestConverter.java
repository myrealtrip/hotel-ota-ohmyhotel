package com.myrealtrip.ohmyhotel.api.application.meta.converter;

import com.myrealtrip.ohmyhotel.enumarate.Language;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.avilability.protocol.request.OmhRoomInfoRequest;
import com.myrealtrip.unionstay.dto.hotelota.meta.room.RoomMetaItem;
import com.myrealtrip.unionstay.dto.hotelota.meta.room.RoomMetaRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class RoomMetaRequestConverter {

    public List<OmhRoomInfoRequest> toOmhRoomInfoRequests(RoomMetaRequest roomMetaRequest) {
        Map<String, RoomMetaItem> distinctRoomMetaItem = roomMetaRequest.getRoomMetaItems().stream()
            .collect(Collectors.toMap(this::getDistinctKey, Function.identity(), (exist, replace) -> exist));

        return distinctRoomMetaItem.values()
            .stream()
            .map(roomMetaItem -> OmhRoomInfoRequest.builder()
                .language(Language.KO)
                .hotelCode(Long.valueOf(roomMetaItem.getProviderPropertyId()))
                .roomTypeCode(roomMetaItem.getProviderRoomId())
                .ratePlanCode(roomMetaItem.getProviderRatePlanId())
                .build())
            .collect(Collectors.toList());
    }

    private String getDistinctKey(RoomMetaItem roomMetaItem) {
        return roomMetaItem.getProviderPropertyId() + roomMetaItem.getProviderRoomId();
    }
}
