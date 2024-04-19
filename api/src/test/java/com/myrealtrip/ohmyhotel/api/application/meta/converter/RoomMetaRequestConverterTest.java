package com.myrealtrip.ohmyhotel.api.application.meta.converter;

import com.myrealtrip.ohmyhotel.outbound.agent.ota.avilability.protocol.request.OmhRoomInfoRequest;
import com.myrealtrip.unionstay.dto.hotelota.meta.room.RoomMetaItem;
import com.myrealtrip.unionstay.dto.hotelota.meta.room.RoomMetaRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class RoomMetaRequestConverterTest {

    private RoomMetaRequestConverter roomMetaRequestConverter = new RoomMetaRequestConverter();

    @Test
    @DisplayName("중복된 요청을 distinct 처리 하여 변환한다. 동일한 (hotelId + roomId) 쌍이 있다면 중복으로 판단 ")
    void toOmhRoomInfoRequests() {
        // given
        RoomMetaRequest roomMetaRequest = RoomMetaRequest.builder()
            .roomMetaItems(List.of(
                roomMetaItem("1", "1", "1"),
                roomMetaItem("1", "1", "2")
            ))
            .build();

        // when
        List<OmhRoomInfoRequest> omhRoomInfoRequests = roomMetaRequestConverter.toOmhRoomInfoRequests(roomMetaRequest);

        // then
        assertThat(omhRoomInfoRequests.size()).isEqualTo(1);
        assertThat(omhRoomInfoRequests.get(0).getHotelCode()).isEqualTo(1L);
        assertThat(omhRoomInfoRequests.get(0).getRoomTypeCode()).isEqualTo("1");
        assertThat(omhRoomInfoRequests.get(0).getRatePlanCode()).isEqualTo("1");
    }

    private RoomMetaItem roomMetaItem(String hotelId, String roomCode, String ratePlanCode) {
        return RoomMetaItem.builder()
            .providerPropertyId(hotelId)
            .providerRoomId(roomCode)
            .providerRatePlanId(ratePlanCode)
            .build();
    }
}