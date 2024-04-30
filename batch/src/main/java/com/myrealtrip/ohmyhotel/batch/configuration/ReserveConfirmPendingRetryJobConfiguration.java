package com.myrealtrip.ohmyhotel.batch.configuration;

import com.myrealtrip.ohmyhotel.batch.UniqueRunIdIncrementer;
import com.myrealtrip.ohmyhotel.batch.listener.ReserveConfirmPendingRetryListener;
import com.myrealtrip.ohmyhotel.batch.reader.ReservationReader;
import com.myrealtrip.ohmyhotel.batch.storage.MrtReservationNoStorage;
import com.myrealtrip.ohmyhotel.batch.writer.ReserveConfirmPendingRetryWriter;
import com.myrealtrip.ohmyhotel.core.domain.reservation.dto.Reservation;
import com.myrealtrip.ohmyhotel.core.provider.reservation.ReservationProvider;
import com.myrealtrip.ohmyhotel.core.service.reservation.BookingMessageKafkaSendService;
import com.myrealtrip.ohmyhotel.core.service.reservation.ReserveConfirmCheckService;
import com.myrealtrip.ohmyhotel.enumarate.ReservationStatus;
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
 * reserve_confirm_pending 상태의 예약을 재시도 한다.
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(name = "spring.batch.job.names", havingValue = ReserveConfirmPendingRetryJobConfiguration.RESERVE_CONFIRM_PENDING_RETRY_JOB)
public class ReserveConfirmPendingRetryJobConfiguration {

    public static final String RESERVE_CONFIRM_PENDING_RETRY_JOB = "RESERVE_CONFIRM_PENDING_RETRY_JOB";
    private static final int CHUNK_SIZE = 1;

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job reserveConfirmPendingRetryJob() {
        return jobBuilderFactory.get(RESERVE_CONFIRM_PENDING_RETRY_JOB)
            .start(reserveConfirmPendingRetryStep(null, null))
            .incrementer(new UniqueRunIdIncrementer())
            .build();
    }

    @Bean
    @JobScope
    public Step reserveConfirmPendingRetryStep(MrtReservationNoStorage mrtReservationNoStorage,
                                               BookingMessageKafkaSendService bookingMessageKafkaSendService) {
        return stepBuilderFactory.get("reserveConfirmPendingRetryStep")
            .transactionManager(transactionManager)
            .<Reservation, Reservation>chunk(CHUNK_SIZE)
            .reader(confirmPendingReservationReader(null))
            .writer(reserveConfirmPendingRetryWriter(null, null, null))
            .listener(new ReserveConfirmPendingRetryListener(mrtReservationNoStorage, bookingMessageKafkaSendService))
            .build();
    }

    @Bean
    @StepScope
    public ItemReader<Reservation> confirmPendingReservationReader(ReservationProvider reservationProvider) {
        return new ReservationReader(reservationProvider, ReservationStatus.RESERVE_CONFIRM_PENDING, CHUNK_SIZE);
    }

    @Bean
    @StepScope
    public ItemWriter<Reservation> reserveConfirmPendingRetryWriter(ReservationProvider reservationProvider,
                                                                    ReserveConfirmCheckService reserveConfirmCheckService,
                                                                    MrtReservationNoStorage mrtReservationNoStorage) {
        return new ReserveConfirmPendingRetryWriter(reservationProvider, reserveConfirmCheckService, mrtReservationNoStorage);
    }

    @Bean
    public MrtReservationNoStorage mrtReservationNoStorage() {
        return new MrtReservationNoStorage();
    }
}
