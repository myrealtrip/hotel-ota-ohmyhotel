package com.myrealtrip.ohmyhotel.configuration.kafka;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Setter
@Getter
@ConfigurationProperties(prefix = "myrealtrip.kafka.common.topics")
public class KafkaTopics {

    private String unionstayPropertyStatusUpdate;

    private String unionstayPropertyUpsert;

    private String unionstayRoomtypeUpsert;

    private String unionstayPropertyRankUpdate;

    private String unionstayBookingDetailUpsert;

    private String unionstayBookingStatusUpdate;

    private String unionstayChainUpsert;

    private String unionstayPropertyMrtDiscountTypesUpdate;
}
