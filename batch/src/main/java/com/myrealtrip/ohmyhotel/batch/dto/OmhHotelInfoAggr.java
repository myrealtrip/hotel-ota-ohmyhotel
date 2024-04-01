package com.myrealtrip.ohmyhotel.batch.dto;


import com.myrealtrip.ohmyhotel.outbound.agent.ota.staticinfo.protocol.response.OmhStaticHotelInfoListResponse.OmhHotelInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class OmhHotelInfoAggr {

    private final OmhHotelInfo koInfo;

    private final OmhHotelInfo enInfo;
}
