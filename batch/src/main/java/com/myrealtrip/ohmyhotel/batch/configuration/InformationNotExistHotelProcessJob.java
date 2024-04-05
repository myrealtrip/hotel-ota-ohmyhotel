package com.myrealtrip.ohmyhotel.batch.configuration;

import com.myrealtrip.ohmyhotel.batch.reader.HotelReader;
import com.myrealtrip.ohmyhotel.batch.writer.InformationNotExistHotelWriter;
import com.myrealtrip.ohmyhotel.core.domain.hotel.dto.Hotel;
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
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * hotel information (상세정보) 조회가 되지 않는 호텔을 INACTIVE 처리 한다.
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(name = "spring.batch.job.names", havingValue = InformationNotExistHotelProcessJob.INFORMATION_NOT_EXIST_HOTEL_PROCESS_JOB)
public class InformationNotExistHotelProcessJob {

    public static final String INFORMATION_NOT_EXIST_HOTEL_PROCESS_JOB = "INFORMATION_NOT_EXIST_HOTEL_PROCESS_JOB";
    private static final int CHUNK_SIZE = 100;

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job hotelSyncJob() {
        return jobBuilderFactory.get(INFORMATION_NOT_EXIST_HOTEL_PROCESS_JOB)
            .start(informationNotExistHotelProcessStep())
            .build();
    }

    @Bean
    @JobScope
    public Step informationNotExistHotelProcessStep() {
        return stepBuilderFactory.get("informationNotExistHotelProcessStep")
            .transactionManager(transactionManager)
            .<Hotel, Hotel>chunk(CHUNK_SIZE)
            .reader(hotelReader(null))
            .writer(informationNotExistHotelWriter(null, null))
            .build();
    }


    @Bean
    @StepScope
    public ItemReader<Hotel> hotelReader(HotelProvider hotelProvider) {
        return new HotelReader(hotelProvider, CHUNK_SIZE);
    }

    @Bean
    @StepScope
    public ItemWriter<Hotel> informationNotExistHotelWriter(HotelProvider hotelProvider,
                                             OmhStaticHotelInfoListAgent omhStaticHotelInfoListAgent) {
        return new InformationNotExistHotelWriter(hotelProvider, omhStaticHotelInfoListAgent);
    }
}
