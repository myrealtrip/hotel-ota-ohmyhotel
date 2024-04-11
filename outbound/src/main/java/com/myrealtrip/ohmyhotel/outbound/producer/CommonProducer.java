package com.myrealtrip.ohmyhotel.outbound.producer;

import com.myrealtrip.ohmyhotel.configuration.kafka.KafkaTopics;
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
        // TODO 통합숙소에서 컨슘할 준비 완료되면 주석 해제
//        kafkaTemplate.send(topics.getUnionstayPropertyUpsert(), String.valueOf(message.getProviderPropertyId()), writeAsString(message));
    }

    public void publishMrtDiscountTypesUpdate(UpdatePropertyMrtDiscountTypesMessage message) {
        // TODO 통합숙소에서 컨슘할 준비 완료되면 주석 해제
//        kafkaTemplate.send(topics.getUnionstayPropertyMrtDiscountTypesUpdate(), writeAsString(message));
    }
}
