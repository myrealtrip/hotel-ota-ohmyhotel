package com.myrealtrip.ohmyhotel.batch.writer;

import com.myrealtrip.ohmyhotel.batch.dto.OmhHotelInfoAggr;
import com.myrealtrip.ohmyhotel.batch.mapper.OmhHotelInfoMapper;
import com.myrealtrip.ohmyhotel.batch.storage.HotelCodeStorage;
import com.myrealtrip.ohmyhotel.core.domain.hotel.dto.Hotel;
import com.myrealtrip.ohmyhotel.core.domain.hotel.dto.HotelModifyInfo;
import com.myrealtrip.ohmyhotel.core.provider.hotel.HotelProvider;
import com.myrealtrip.ohmyhotel.enumarate.Language;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.staticinfo.OmhStaticHotelInfoListAgent;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.staticinfo.protocol.request.OmhStaticHotelInfoListRequest;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.staticinfo.protocol.response.OmhStaticHotelInfoListResponse.OmhHotelInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemWriter;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 호텔 상세정보를 DB 에 저장한다.
 */
@RequiredArgsConstructor
public class HotelInfoWriter implements ItemWriter<Long> {

    private final HotelProvider hotelProvider;
    private final OmhHotelInfoMapper omhHotelInfoMapper;
    private final OmhStaticHotelInfoListAgent omhStaticHotelInfoListAgent;


    @Override
    public void write(List<? extends Long> hotelCodes) throws Exception {
        List<OmhHotelInfoAggr> omhHotelInfoAggrs = getOmhHotelInfoAggrs((List<Long>) hotelCodes);

        List<Long> hotelIds = omhHotelInfoAggrs.stream()
            .map(OmhHotelInfoAggr::getHotelCode)
            .collect(Collectors.toList());

        Map<Long, HotelModifyInfo> hotelModifyInfoMap = hotelProvider.getHotelModifyInfoByHotelIds(hotelIds).stream()
            .collect(Collectors.toMap(HotelModifyInfo::getHotelId, Function.identity()));

        List<Hotel> hotels = omhHotelInfoAggrs.stream()
            .map(omhHotelInfoAggr -> omhHotelInfoMapper.toHotel(omhHotelInfoAggr, hotelModifyInfoMap.get(omhHotelInfoAggr.getHotelCode())))
            .collect(Collectors.toList());

        hotelProvider.upsert(hotels);
    }

    private List<OmhHotelInfoAggr> getOmhHotelInfoAggrs(List<Long> hotelCodes) {
        List<OmhHotelInfo> koOmhHotelInfoList = omhStaticHotelInfoListAgent.getHotelInfo(OmhStaticHotelInfoListRequest.create(Language.KO, (List<Long>) hotelCodes))
            .getHotels();

        Map<Long, OmhHotelInfo> enOmhHotelInfoMap = omhStaticHotelInfoListAgent.getHotelInfo(OmhStaticHotelInfoListRequest.create(Language.EN, (List<Long>) hotelCodes))
            .getHotels()
            .stream()
            .collect(Collectors.toMap(OmhHotelInfo::getHotelCode, Function.identity()));

        return koOmhHotelInfoList.stream()
            .map(koOmhHotelInfo -> new OmhHotelInfoAggr(koOmhHotelInfo, enOmhHotelInfoMap.get(koOmhHotelInfo.getHotelCode())))
            .collect(Collectors.toList());
    }
}
