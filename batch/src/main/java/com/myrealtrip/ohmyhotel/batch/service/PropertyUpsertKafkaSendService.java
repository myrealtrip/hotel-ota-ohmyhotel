package com.myrealtrip.ohmyhotel.batch.service;

import com.myrealtrip.ohmyhotel.batch.mapper.UpsertPropertyMessageMapper;
import com.myrealtrip.ohmyhotel.core.domain.hotel.dto.Hotel;
import com.myrealtrip.ohmyhotel.core.domain.zeromargin.dto.ZeroMargin;
import com.myrealtrip.ohmyhotel.core.provider.hotel.HotelProvider;
import com.myrealtrip.ohmyhotel.core.service.ZeroMarginSearchService;
import com.myrealtrip.ohmyhotel.outbound.producer.CommonProducer;
import com.myrealtrip.srtcommon.support.utils.ObjectMapperUtils;
import com.myrealtrip.unionstay.common.message.property.UpsertPropertyMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PropertyUpsertKafkaSendService {

    @Value("${ohmyhotel.partner-id}")
    private Long partnerId;

    private final CommonProducer commonProducer;
    private final HotelProvider hotelProvider;
    private final UpsertPropertyMessageMapper upsertPropertyMessageMapper;
    private final ZeroMarginSearchService zeroMarginSearchService;

    public void sendByHotelIds(List<Long> hotelIds) {
        List<Hotel> hotels = hotelProvider.getByHotelIds(hotelIds);
        sendByHotels(hotels);
    }

    public void sendByHotels(List<Hotel> hotels) {
        List<Long> hotelIds = hotels.stream()
            .map(Hotel::getHotelId)
            .collect(Collectors.toList());

        Map<Long, ZeroMargin> zeroMarginMap = zeroMarginSearchService.getZeroMargins(hotelIds, false);
        List<UpsertPropertyMessage> messages = hotels.stream()
            .map(hotel -> {
                ZeroMargin zeroMargin = zeroMarginMap.get(hotel.getHotelId());
                return upsertPropertyMessageMapper.toUpsertPropertyMessage(hotel, zeroMargin.isOn(), partnerId);
            })
            .collect(Collectors.toList());

        send(messages);
    }

    private void send(List<UpsertPropertyMessage> upsertPropertyMessageList) {
        if (CollectionUtils.isEmpty(upsertPropertyMessageList)) {
            return;
        }
        for (UpsertPropertyMessage upsertPropertyMessage : upsertPropertyMessageList) {
            commonProducer.publishPropertyUpsert(upsertPropertyMessage);
        }

        String ids = upsertPropertyMessageList.stream()
            .map(UpsertPropertyMessage::getProviderPropertyId)
            .collect(Collectors.joining(", "));

        log.info("property upsert send");
        log.info("{}", ids);
    }
}
