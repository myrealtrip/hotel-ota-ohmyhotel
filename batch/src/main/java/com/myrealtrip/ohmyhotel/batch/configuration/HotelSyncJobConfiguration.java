package com.myrealtrip.ohmyhotel.batch.configuration;

import com.myrealtrip.ohmyhotel.batch.storage.HotelCodeStorage;
import com.myrealtrip.ohmyhotel.batch.tasklet.GetUpdatedHotelCodesTasklet;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.staticinfo.OmhStaticHotelBulkListAgent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManager;
import java.time.LocalDate;

import static java.util.Objects.isNull;

@Slf4j
@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(name = "spring.batch.job.names", havingValue = HotelSyncJobConfiguration.HOTEL_SYNC_JOB)
public class HotelSyncJobConfiguration {

    public static final String HOTEL_SYNC_JOB = "HOTEL_SYNC_JOB";

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final PlatformTransactionManager transactionManager;
    private final EntityManager entityManager;

    @Bean
    @JobScope
    public Step getUpdatedHotelCodesStep() {
        return stepBuilderFactory.get("getUpdatedHotelCodesStep")
            .transactionManager(transactionManager)
            .tasklet(getUpdatedHotelCodesTasklet(null, null, null))
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
    public HotelCodeStorage hotelCodeStorage() {
        return new HotelCodeStorage();
    }
}
