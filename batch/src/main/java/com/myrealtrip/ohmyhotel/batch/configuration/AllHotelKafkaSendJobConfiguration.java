package com.myrealtrip.ohmyhotel.batch.configuration;

import com.myrealtrip.ohmyhotel.batch.reader.HotelReader;
import com.myrealtrip.ohmyhotel.batch.service.PropertyUpsertKafkaSendService;
import com.myrealtrip.ohmyhotel.core.domain.hotel.dto.Hotel;
import com.myrealtrip.ohmyhotel.core.provider.hotel.HotelProvider;
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


/**
 * 모든 호텔 정보를 카프카로 전송한다.
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(name = "spring.batch.job.names", havingValue = AllHotelKafkaSendJobConfiguration.ALL_HOTEL_KAFKA_SEND_JOB)
public class AllHotelKafkaSendJobConfiguration {

    public static final String ALL_HOTEL_KAFKA_SEND_JOB = "ALL_HOTEL_KAFKA_SEND_JOB";
    private static final int CHUNK_SIZE = 100;

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job allHotelKafkaSendJob() {
        return jobBuilderFactory.get(ALL_HOTEL_KAFKA_SEND_JOB)
            .start(allHotelKafkaSendStep())
            .build();
    }

    @Bean
    @JobScope
    public Step allHotelKafkaSendStep() {
        return stepBuilderFactory.get("allHotelKafkaSendStep")
            .transactionManager(transactionManager)
            .<Hotel, Hotel>chunk(CHUNK_SIZE)
            .reader(hotelReader(null))
            .writer(hotelKafkaSendWriter(null))
            .build();
    }

    @Bean
    @StepScope
    public ItemWriter<Hotel> hotelKafkaSendWriter(PropertyUpsertKafkaSendService propertyUpsertKafkaSendService) {
        return hotels -> propertyUpsertKafkaSendService.sendByHotels((List<Hotel>) hotels);
    }

    @Bean
    @StepScope
    public ItemReader<Hotel> hotelReader(HotelProvider hotelProvider) {
        return new HotelReader(hotelProvider, CHUNK_SIZE);
    }
}
