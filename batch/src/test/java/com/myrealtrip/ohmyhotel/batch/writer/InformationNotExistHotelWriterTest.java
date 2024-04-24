package com.myrealtrip.ohmyhotel.batch.writer;

import com.myrealtrip.ohmyhotel.batch.storage.HotelCodeStorage;
import com.myrealtrip.ohmyhotel.core.domain.hotel.dto.Hotel;
import com.myrealtrip.ohmyhotel.core.provider.hotel.HotelProvider;
import com.myrealtrip.ohmyhotel.enumarate.HotelStatus;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.staticinfo.OmhStaticHotelInfoListAgent;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.staticinfo.protocol.response.OmhStaticHotelInfoListResponse;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.staticinfo.protocol.response.OmhStaticHotelInfoListResponse.OmhHotelInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class InformationNotExistHotelWriterTest {

    @InjectMocks
    private InformationNotExistHotelWriter writer;

    @Mock private HotelProvider hotelProvider;
    @Mock private OmhStaticHotelInfoListAgent agent;
    @Spy private HotelCodeStorage chunkUpdatedHotelCodeStorage = new HotelCodeStorage();

    @Test
    @DisplayName("information 정보가 조회되지 않는 호텔을 inactive 상태로 변경한다.")
    void update_status_to_inactive() throws Exception {
        // given
        List<Hotel> hotels = List.of(
            Hotel.builder().hotelId(1L).build(),
            Hotel.builder().hotelId(2L).build()
        );

        OmhStaticHotelInfoListResponse response = OmhStaticHotelInfoListResponse.builder()
            .hotels(List.of(OmhHotelInfo.builder().hotelCode(1L).build()))
            .build();
        given(agent.getHotelInfo(any())).willReturn(response);

        // when
        writer.write(hotels);

        // then
        verify(hotelProvider, times(1)).updateStatusByHotelIds(List.of(2L), HotelStatus.INACTIVE);
    }
}