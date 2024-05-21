package com.myrealtrip.ohmyhotel.outbound.producer;

import com.myrealtrip.ohmyhotel.configuration.kafka.KafkaTopics;
import com.myrealtrip.srtcommon.support.utils.ObjectMapperUtils;
import com.myrealtrip.unionstay.common.message.booking.UpsertBookingDetailMessage;
import com.myrealtrip.unionstay.common.message.property.UpdatePropertyMrtDiscountTypesMessage;
import com.myrealtrip.unionstay.common.message.property.UpsertPropertyMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import static com.myrealtrip.srtcommon.support.utils.ObjectMapperUtils.writeAsString;

@Component
@RequiredArgsConstructor
public class CommonProducer {

    private final KafkaTopics topics;

    private final KafkaTemplate<String, String> kafkaTemplate;

    public void publishPropertyUpsert(UpsertPropertyMessage message) {
         // kafkaTemplate.send(topics.getUnionstayPropertyUpsert(), String.valueOf(message.getProviderPropertyId()), writeAsString(message));
    }

    public void publishMrtDiscountTypesUpdate(UpdatePropertyMrtDiscountTypesMessage message) {
         // kafkaTemplate.send(topics.getUnionstayPropertyMrtDiscountTypesUpdate(), writeAsString(message));
    }

    public void publishUpsertBookingDetail(UpsertBookingDetailMessage message) {
         // kafkaTemplate.send(topics.getUnionstayBookingDetailUpsert(), message.getMrtReservationNo(), ObjectMapperUtils.writeAsString(message));
    }
}
