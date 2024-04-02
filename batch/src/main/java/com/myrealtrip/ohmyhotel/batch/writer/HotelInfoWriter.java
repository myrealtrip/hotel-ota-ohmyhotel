package com.myrealtrip.ohmyhotel.batch.writer;

import com.myrealtrip.ohmyhotel.batch.dto.OmhHotelInfoAggr;
import com.myrealtrip.ohmyhotel.batch.mapper.OmhHotelInfoMapper;
import com.myrealtrip.ohmyhotel.core.domain.hotel.dto.Hotel;
import com.myrealtrip.ohmyhotel.core.domain.hotel.dto.HotelModifyInfo;
import com.myrealtrip.ohmyhotel.core.provider.hotel.HotelProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemWriter;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class HotelInfoWriter implements ItemWriter<OmhHotelInfoAggr> {

    private final HotelProvider hotelProvider;
    private final OmhHotelInfoMapper omhHotelInfoMapper;


    @Override
    public void write(List<? extends OmhHotelInfoAggr> omhHotelInfoAggrs) throws Exception {
        List<Long> hotelIds = omhHotelInfoAggrs.stream()
            .map(omhHotelInfoAggr -> omhHotelInfoAggr.getHotelCode())
            .collect(Collectors.toList());

        Map<Long, HotelModifyInfo> hotelModifyInfoMap = hotelProvider.getHotelModifyInfoByHotelIds(hotelIds).stream()
            .collect(Collectors.toMap(HotelModifyInfo::getHotelId, Function.identity()));

        List<Hotel> hotels = omhHotelInfoAggrs.stream()
            .map(omhHotelInfoAggr -> omhHotelInfoMapper.toHotel(omhHotelInfoAggr, hotelModifyInfoMap.get(omhHotelInfoAggr.getHotelCode())))
            .collect(Collectors.toList());

        hotelProvider.upsert(hotels);
    }
}
