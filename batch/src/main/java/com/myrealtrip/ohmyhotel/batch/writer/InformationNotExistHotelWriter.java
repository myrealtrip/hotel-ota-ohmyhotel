package com.myrealtrip.ohmyhotel.batch.writer;

import com.myrealtrip.ohmyhotel.batch.storage.HotelCodeStorage;
import com.myrealtrip.ohmyhotel.core.domain.hotel.dto.Hotel;
import com.myrealtrip.ohmyhotel.core.provider.hotel.HotelProvider;
import com.myrealtrip.ohmyhotel.enumarate.HotelStatus;
import com.myrealtrip.ohmyhotel.enumarate.Language;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.staticinfo.OmhStaticHotelInfoListAgent;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.staticinfo.protocol.request.OmhStaticHotelInfoListRequest;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.staticinfo.protocol.response.OmhStaticHotelInfoListResponse.OmhHotelInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemWriter;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class InformationNotExistHotelWriter implements ItemWriter<Hotel> {

    private final HotelProvider hotelProvider;
    private final OmhStaticHotelInfoListAgent omhStaticHotelInfoListAgent;
    private final HotelCodeStorage chunkUpdatedHotelCodeStorage;

    @Override
    public void write(List<? extends Hotel> hotels) throws Exception {
        List<Long> hotelIds = hotels.stream()
            .map(Hotel::getHotelId)
            .collect(Collectors.toList());

        List<Long> informationNotExistHotelIds = filterInformationNotExistHotelIds(hotelIds);

        hotelProvider.updateStatusByHotelIds(informationNotExistHotelIds, HotelStatus.INACTIVE);
        chunkUpdatedHotelCodeStorage.clear();
        chunkUpdatedHotelCodeStorage.saveAll(informationNotExistHotelIds);
    }

    private List<Long> filterInformationNotExistHotelIds(List<Long> hotelIds) {
        OmhStaticHotelInfoListRequest request = OmhStaticHotelInfoListRequest.builder()
            .language(Language.KO)
            .hotelCodes(hotelIds)
            .lastUpdateDate(LocalDate.of(1970, 1, 1))
            .build();

        Set<Long> informationExistHotelIds = omhStaticHotelInfoListAgent.getHotelInfo(request).getHotels()
            .stream()
            .map(OmhHotelInfo::getHotelCode)
            .collect(Collectors.toSet());

        return hotelIds.stream()
            .filter(hotelId -> !informationExistHotelIds.contains(hotelId))
            .collect(Collectors.toList());
    }
}
