package com.myrealtrip.ohmyhotel.batch.service;

import com.myrealtrip.ohmyhotel.batch.mapper.UpsertPropertyMessageMapper;
import com.myrealtrip.ohmyhotel.core.provider.hotel.HotelProvider;
import com.myrealtrip.ohmyhotel.outbound.producer.CommonProducer;
import com.myrealtrip.unionstay.common.message.property.UpsertPropertyMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PropertyUpsertKafkaSendService {

    private final CommonProducer commonProducer;
    private final HotelProvider hotelProvider;
    private final UpsertPropertyMessageMapper upsertPropertyMessageMapper;

    public void sendByHotelIds(List<Long> hotelIds) {
        List<UpsertPropertyMessage> messages = hotelProvider.getByHotelIds(hotelIds)
            .stream()
            .map(upsertPropertyMessageMapper::toUpsertPropertyMessage)
            .collect(Collectors.toList());

        send(messages);
    }

    private void send(List<UpsertPropertyMessage> upsertPropertyMessageList) {
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
