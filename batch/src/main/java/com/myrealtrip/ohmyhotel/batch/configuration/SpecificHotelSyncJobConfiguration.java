package com.myrealtrip.ohmyhotel.batch.configuration;

import com.myrealtrip.ohmyhotel.batch.listener.HotelUpdateChunkListener;
import com.myrealtrip.ohmyhotel.batch.mapper.OmhHotelInfoMapper;
import com.myrealtrip.ohmyhotel.batch.reader.HotelCodeStorageReader;
import com.myrealtrip.ohmyhotel.batch.service.PropertyUpsertKafkaSendService;
import com.myrealtrip.ohmyhotel.batch.storage.HotelCodeStorage;
import com.myrealtrip.ohmyhotel.batch.tasklet.NotFoundHotelCodesLoggingTasklet;
import com.myrealtrip.ohmyhotel.batch.writer.HotelInfoWriter;
import com.myrealtrip.ohmyhotel.core.provider.hotel.HotelProvider;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.staticinfo.OmhStaticHotelInfoListAgent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 특정 호텔을 오마이호텔로부터 싱크한다.
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(name = "spring.batch.job.names", havingValue = SpecificHotelSyncJobConfiguration.SPECIFIC_HOTEL_SYNC_JOB)
public class SpecificHotelSyncJobConfiguration {

    public static final String SPECIFIC_HOTEL_SYNC_JOB = "SPECIFIC_HOTEL_SYNC_JOB";
    private static final int CHUNK_SIZE = 100;

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job specificHotelSyncJob() {
        return jobBuilderFactory.get(SPECIFIC_HOTEL_SYNC_JOB)
            .start(specificHotelSyncStep(null, null))
            .next(loggingStep())
            .build();
    }

    @Bean
    @JobScope
    public Step specificHotelSyncStep(@Qualifier("chunkUpdatedHotelCodeStorage") HotelCodeStorage chunkUpdatedHotelCodeStorage,
                                      PropertyUpsertKafkaSendService propertyUpsertKafkaSendService) {
        return stepBuilderFactory.get("specificHotelSyncStep")
            .transactionManager(transactionManager)
            .<Long, Long>chunk(CHUNK_SIZE)
            .reader(hotelCodeStorageReader(null, null))
            .writer(hotelInfoWriter(null, null, null, null, null))
            .listener(new HotelUpdateChunkListener(chunkUpdatedHotelCodeStorage, propertyUpsertKafkaSendService))
            .build();
    }

    @Bean
    @JobScope
    public Step loggingStep() {
        return stepBuilderFactory.get("loggingStep")
            .transactionManager(transactionManager)
            .tasklet(loggingTasklet(null))
            .build();
    }

    @Bean
    @StepScope
    public ItemReader<Long> hotelCodeStorageReader(@Value("#{jobParameters[hotelIds]}") String hotelIdsParam,
                                                   @Qualifier("allHotelCodeStorage") HotelCodeStorage allHotelCodeStorage) {
        List<Long> hotelIds = Arrays.stream(hotelIdsParam.replaceAll(" ", "").split(","))
            .distinct()
            .map(Long::valueOf)
            .collect(Collectors.toList());
        allHotelCodeStorage.addAll(hotelIds);
        return new HotelCodeStorageReader(allHotelCodeStorage, CHUNK_SIZE);
    }

    @Bean
    @StepScope
    public ItemWriter<Long> hotelInfoWriter(HotelProvider hotelProvider,
                                            OmhHotelInfoMapper omhHotelInfoMapper,
                                            OmhStaticHotelInfoListAgent omhStaticHotelInfoListAgent,
                                            @Qualifier("chunkUpdatedHotelCodeStorage") HotelCodeStorage chunkUpdatedHotelCodeStorage,
                                            @Qualifier("notFoundHotelCodeStorage") HotelCodeStorage notFoundHotelCodeStorage) {
        return new HotelInfoWriter(hotelProvider, omhHotelInfoMapper, omhStaticHotelInfoListAgent, chunkUpdatedHotelCodeStorage, notFoundHotelCodeStorage);
    }

    @Bean
    @StepScope
    public Tasklet loggingTasklet(@Qualifier("notFoundHotelCodeStorage") HotelCodeStorage notFindHotelCodeStorage) {
        return new NotFoundHotelCodesLoggingTasklet(notFindHotelCodeStorage);
    }

    /* 오마이호텔에서 제공하는 전체 호텔 코드 */
    @Bean(name = "allHotelCodeStorage")
    public HotelCodeStorage updatedHotelCodeStorage() {
        return new HotelCodeStorage();
    }

    /* 매 청크마다 업데이트 하는 호텔 코드 */
    @Bean(name = "chunkUpdatedHotelCodeStorage")
    public HotelCodeStorage chunkUpdatedHotelCodeStorage() {
        return new HotelCodeStorage();
    }

    /* 오마이호텔에서 제공하는 전체 호텔 코드 중 상세정보가 검색되지 않는 호텔 코드 */
    @Bean(name = "notFoundHotelCodeStorage")
    public HotelCodeStorage notFoundHotelCodeStorage() {
        return new HotelCodeStorage();
    }
}
