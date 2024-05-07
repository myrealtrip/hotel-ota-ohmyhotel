package com.myrealtrip.ohmyhotel.batch.configuration;


import com.myrealtrip.ohmyhotel.batch.reader.ConfirmAndNotCheckInReservationReader;
import com.myrealtrip.ohmyhotel.batch.storage.MrtReservationNoStorage;
import com.myrealtrip.ohmyhotel.batch.tasklet.HotelCancelAlertTasklet;
import com.myrealtrip.ohmyhotel.batch.writer.HotelCancelCheckWriter;
import com.myrealtrip.ohmyhotel.core.domain.reservation.dto.Reservation;
import com.myrealtrip.ohmyhotel.core.provider.reservation.ReservationProvider;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.reservation.OmhBookingDetailAgent;
import com.myrealtrip.slack.client.SlackNotifier;
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

/**
 * 호텔에서 먼저 취소된 예약건이 있는지 체크한다.
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(name = "spring.batch.job.names", havingValue = HotelCancelCheckJobConfiguration.HOTEL_CANCEL_CHECK_JOB)
public class HotelCancelCheckJobConfiguration {

    public static final String HOTEL_CANCEL_CHECK_JOB = "HOTEL_CANCEL_CHECK_JOB";
    private static final int CHUNK_SIZE = 100;

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job hotelCancelCheckJob() {
        return jobBuilderFactory.get(HOTEL_CANCEL_CHECK_JOB)
            .start(hotelCancelCheckStep())
            .next(hotelCancelAlertStep())
            .build();
    }

    @Bean
    @JobScope
    public Step hotelCancelCheckStep() {
        return stepBuilderFactory.get("hotelCancelCheckStep")
            .transactionManager(transactionManager)
            .<Reservation, Reservation>chunk(CHUNK_SIZE)
            .reader(confirmAndNotCheckInReservationReader(null))
            .writer(hotelCancelCheckWriter(null, null))
            .build();
    }

    @Bean
    @JobScope
    public Step hotelCancelAlertStep() {
        return stepBuilderFactory.get("hotelCancelAlertStep")
            .tasklet(hotelCancelAlertTasklet(null, null, null, null))
            .build();
    }

    @Bean
    @StepScope
    public Tasklet hotelCancelAlertTasklet(@Qualifier("reservationCxSlackNotifier") SlackNotifier reservationCxSlackNotifier,
                                           @Value("${spring.profiles.active:none}") String profile,
                                           @Value("${myrealtrip.ohmyhotel.context:none}") String context,
                                           MrtReservationNoStorage mrtReservationNoStorage) {
        return new HotelCancelAlertTasklet(reservationCxSlackNotifier, profile, context, mrtReservationNoStorage);
    }

    @Bean
    @StepScope
    public ItemWriter<Reservation> hotelCancelCheckWriter(OmhBookingDetailAgent omhBookingDetailAgent,
                                                          MrtReservationNoStorage mrtReservationNoStorage) {
        return new HotelCancelCheckWriter(omhBookingDetailAgent, mrtReservationNoStorage);
    }

    @Bean
    @StepScope
    public ItemReader<Reservation> confirmAndNotCheckInReservationReader(ReservationProvider reservationProvider) {
        return new ConfirmAndNotCheckInReservationReader(reservationProvider, CHUNK_SIZE);
    }

    @Bean
    public MrtReservationNoStorage mrtReservationNoStorage() {
        return new MrtReservationNoStorage();
    }
}
