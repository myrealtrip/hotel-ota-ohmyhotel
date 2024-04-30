package com.myrealtrip.ohmyhotel.batch.configuration;

import com.myrealtrip.ohmyhotel.batch.reader.HotelReader;
import com.myrealtrip.ohmyhotel.core.domain.hotel.dto.Hotel;
import com.myrealtrip.ohmyhotel.core.provider.hotel.HotelProvider;
import com.myrealtrip.ohmyhotel.core.service.MrtDiscountTypesUpdateKafkaSendService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 모든 호텔의 mrtDiscountType 을 카프카로 전송합니다.
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(name = "spring.batch.job.names", havingValue = AllHotelMrtDiscountTypesKafkaSendJobConfiguration.ALL_HOTEL_MRT_DISCOUNT_TYPES_KAFKA_SEND_JOB)
public class AllHotelMrtDiscountTypesKafkaSendJobConfiguration {

    public static final String ALL_HOTEL_MRT_DISCOUNT_TYPES_KAFKA_SEND_JOB = "ALL_HOTEL_MRT_DISCOUNT_TYPES_KAFKA_SEND_JOB";
    private static final int CHUNK_SIZE = 100;

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job allHotelMrtDiscountTypesKafkaSendJob() {
        return jobBuilderFactory.get(ALL_HOTEL_MRT_DISCOUNT_TYPES_KAFKA_SEND_JOB)
            .start(allHotelMrtDiscountTypesKafkaSendStep())
            .build();
    }

    @Bean
    @JobScope
    public Step allHotelMrtDiscountTypesKafkaSendStep() {
        return stepBuilderFactory.get("allHotelMrtDiscountTypesKafkaSendStep")
            .transactionManager(transactionManager)
            .<Hotel, Hotel>chunk(CHUNK_SIZE)
            .reader(hotelReader(null))
            .writer(mrtDiscountTypesKafkaSendWriter(null))
            .build();
    }

    @Bean
    @StepScope
    public ItemReader<Hotel> hotelReader(HotelProvider hotelProvider) {
        return new HotelReader(hotelProvider, CHUNK_SIZE);
    }

    @Bean
    @StepScope
    public ItemWriter<Hotel> mrtDiscountTypesKafkaSendWriter(MrtDiscountTypesUpdateKafkaSendService mrtDiscountTypesUpdateKafkaSendService) {
        return hotels -> {
            List<Long> hotelIds = hotels.stream()
                .map(Hotel::getHotelId)
                .collect(Collectors.toList());
            mrtDiscountTypesUpdateKafkaSendService.sendByHotelIds(hotelIds);
        };
    }
}
