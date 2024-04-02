package com.myrealtrip.ohmyhotel.batch.configuration;

import com.myrealtrip.ohmyhotel.batch.dto.OmhHotelInfoAggr;
import com.myrealtrip.ohmyhotel.batch.mapper.OmhHotelInfoMapper;
import com.myrealtrip.ohmyhotel.batch.reader.HotelInfoReader;
import com.myrealtrip.ohmyhotel.batch.storage.HotelCodeStorage;
import com.myrealtrip.ohmyhotel.batch.tasklet.GetUpdatedHotelCodesTasklet;
import com.myrealtrip.ohmyhotel.batch.writer.HotelInfoWriter;
import com.myrealtrip.ohmyhotel.core.provider.hotel.HotelProvider;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.staticinfo.OmhStaticHotelBulkListAgent;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDate;

import static java.util.Objects.isNull;

@Slf4j
@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(name = "spring.batch.job.names", havingValue = HotelSyncJobConfiguration.HOTEL_SYNC_JOB)
public class HotelSyncJobConfiguration {

    public static final String HOTEL_SYNC_JOB = "HOTEL_SYNC_JOB";
    private static final int CHUNK_SIZE = 100;

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job hotelSyncJob() {
        return jobBuilderFactory.get(HOTEL_SYNC_JOB)
            .start(getUpdatedHotelCodesStep())
            .next(hotelUpsertStep())
            .build();
    }

    @Bean
    @JobScope
    public Step getUpdatedHotelCodesStep() {
        return stepBuilderFactory.get("getUpdatedHotelCodesStep")
            .transactionManager(transactionManager)
            .tasklet(getUpdatedHotelCodesTasklet(null, null, null))
            .build();
    }

    @Bean
    @JobScope
    public Step hotelUpsertStep() {
        return stepBuilderFactory.get("hotelUpsertStep")
            .transactionManager(transactionManager)
            .<OmhHotelInfoAggr, OmhHotelInfoAggr>chunk(CHUNK_SIZE)
            .reader(hotelInfoReader(null, null))
            .writer(hotelInfoWriter(null, null))
            .build();
    }

    @Bean
    @StepScope
    public Tasklet getUpdatedHotelCodesTasklet(@Value("#{jobParameters[beforeDays]}") Integer beforeDays,
                                               HotelCodeStorage hotelCodeStorage,
                                               OmhStaticHotelBulkListAgent omhStaticHotelBulkListAgent) {
        LocalDate lastUpdatedDate = isNull(beforeDays) ?
                                    LocalDate.of(1970, 1, 1) :
                                    LocalDate.now().minusDays(beforeDays);
        return new GetUpdatedHotelCodesTasklet(hotelCodeStorage, omhStaticHotelBulkListAgent, lastUpdatedDate);
    }

    @Bean
    @StepScope
    public ItemReader<OmhHotelInfoAggr> hotelInfoReader(OmhStaticHotelInfoListAgent omhStaticHotelInfoListAgent,
                                                        HotelCodeStorage hotelCodeStorage) {
        return new HotelInfoReader(omhStaticHotelInfoListAgent, hotelCodeStorage, CHUNK_SIZE);
    }

    @Bean
    @StepScope
    public ItemWriter<OmhHotelInfoAggr> hotelInfoWriter(HotelProvider hotelProvider,
                                                        OmhHotelInfoMapper omhHotelInfoMapper) {
        return new HotelInfoWriter(hotelProvider, omhHotelInfoMapper);
    }

    @Bean
    public HotelCodeStorage hotelCodeStorage() {
        return new HotelCodeStorage();
    }
}
