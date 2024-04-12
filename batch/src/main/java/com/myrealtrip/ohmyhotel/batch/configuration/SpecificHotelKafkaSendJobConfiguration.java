package com.myrealtrip.ohmyhotel.batch.configuration;

import com.myrealtrip.ohmyhotel.batch.reader.HotelCodeStorageReader;
import com.myrealtrip.ohmyhotel.batch.service.PropertyUpsertKafkaSendService;
import com.myrealtrip.ohmyhotel.batch.storage.HotelCodeStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


/**
 * 특정 호텔 정보를 카프카로 전송한다.
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(name = "spring.batch.job.names", havingValue = SpecificHotelKafkaSendJobConfiguration.SPECIFIC_HOTEL_KAFKA_SEND_JOB)
public class SpecificHotelKafkaSendJobConfiguration {

    public static final String SPECIFIC_HOTEL_KAFKA_SEND_JOB = "SPECIFIC_HOTEL_KAFKA_SEND_JOB";
    private static final int CHUNK_SIZE = 100;

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job specificHotelKafkaSendJob() {
        return jobBuilderFactory.get(SPECIFIC_HOTEL_KAFKA_SEND_JOB)
            .start(specificHotelKafkaSendStep())
            .build();
    }

    @Bean
    @JobScope
    public Step specificHotelKafkaSendStep() {
        return stepBuilderFactory.get("specificHotelKafkaSendStep")
            .transactionManager(transactionManager)
            .<Long, Long>chunk(CHUNK_SIZE)
            .reader(hotelCodeStorageReader(null, null))
            .writer(hotelKafkaSendWriter(null))
            .build();
    }

    @Bean
    @StepScope
    public ItemWriter<Long> hotelKafkaSendWriter(PropertyUpsertKafkaSendService propertyUpsertKafkaSendService) {
        return hotelIds -> propertyUpsertKafkaSendService.sendByHotelIds((List<Long>) hotelIds);
    }

    @Bean
    @StepScope
    public ItemReader<Long> hotelCodeStorageReader(@Value("#{jobParameters[hotelIds]}") String hotelIdsParam,
                                                   HotelCodeStorage hotelCodeStorage) {
        List<Long> hotelIds = Arrays.stream(hotelIdsParam.replaceAll(" ", "").split(","))
            .distinct()
            .map(Long::valueOf)
            .collect(Collectors.toList());
        hotelCodeStorage.saveAll(hotelIds);
        return new HotelCodeStorageReader(hotelCodeStorage, CHUNK_SIZE);
    }

    @Bean
    public HotelCodeStorage hotelCodeStorage() {
        return new HotelCodeStorage();
    }
}
