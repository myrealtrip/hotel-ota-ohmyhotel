package com.myrealtrip.ohmyhotel.batch.writer;

import com.myrealtrip.ohmyhotel.batch.storage.MrtReservationNoStorage;
import com.myrealtrip.ohmyhotel.core.domain.reservation.dto.Reservation;
import com.myrealtrip.ohmyhotel.core.provider.reservation.ReservationProvider;
import com.myrealtrip.ohmyhotel.core.service.reservation.ReserveConfirmCheckService;
import com.myrealtrip.ohmyhotel.enumarate.ReservationStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemWriter;

import java.util.List;

/**
 * reserve_confirm_pending 상태의 예약을 retry 한다. </p>
 * 청크 사이즈를 1로 설정하는 이유: </p>
 * - LOCK 이 걸리는 시간을 최소화 함
 * - 처리 완료된 예약이 다른 예약건에 의해 롤백이 발생하는 것을 막기 위함
 */
@Slf4j
@RequiredArgsConstructor
public class ReserveConfirmPendingRetryWriter implements ItemWriter<Reservation> {

    private static final int MAX_RETRY_COUNT = 10;

    private final ReservationProvider reservationProvider;
    private final ReserveConfirmCheckService reserveConfirmCheckService;
    private final MrtReservationNoStorage mrtReservationNoStorage;

    @Override
    public void write(List<? extends Reservation> reservations) throws Exception {
        if (reservations.size() > 1) {
            throw new IllegalStateException("청크 사이즈를 1로 설정해주세요.");
        }
        mrtReservationNoStorage.clear();
        for (Reservation reservation : reservations) {
            confirmPendingRetry(reservation.getMrtReservationNo());
        }
    }

    public void confirmPendingRetry(String mrtReservationNo) {
        /* 배치와 컨슈머에서 예약이 동시 처리되는 것을 막기 위해 LOCk 을 걸고 처리한다. */
        Reservation reservation = reservationProvider.getByMrtReservationNoWithLock(mrtReservationNo);
        if (reservation.getReservationStatus() != ReservationStatus.RESERVE_CONFIRM_PENDING ||
            reservation.getConfirmPendingRetryCount() >= MAX_RETRY_COUNT) {
            return;
        }
        log.info("{} - retry...", mrtReservationNo);
        reserveConfirmCheckService.checkOmhBookDetailAndUpdateReservation(reservation);
        reservationProvider.addConfirmPendingRetryCount(reservation.getReservationId());
        mrtReservationNoStorage.addAll(List.of(mrtReservationNo));
    }
}
