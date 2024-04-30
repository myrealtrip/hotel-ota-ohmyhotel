package com.myrealtrip.ohmyhotel.batch.listener;

import com.myrealtrip.ohmyhotel.batch.storage.MrtReservationNoStorage;
import com.myrealtrip.ohmyhotel.core.service.reservation.BookingMessageKafkaSendService;
import com.myrealtrip.ohmyhotel.enumarate.ReservationStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.scope.context.ChunkContext;

import java.util.List;

/**
 * confirm_pending 상태의 예약건 retry 청크가 완료되면 kafka 메세지를 전송한다.
 */
@RequiredArgsConstructor
public class ReserveConfirmPendingRetryListener implements ChunkListener {

    private final MrtReservationNoStorage mrtReservationNoStorage;
    private final BookingMessageKafkaSendService bookingMessageKafkaSendService;

    @Override
    public void beforeChunk(ChunkContext context) {

    }

    @Override
    public void afterChunk(ChunkContext context) {
        for (String mrtReservationNo : mrtReservationNoStorage.getMrtReservationNos())
            bookingMessageKafkaSendService.sendByMrtReservationNo(
                mrtReservationNo,
                List.of(ReservationStatus.RESERVE_CONFIRM, ReservationStatus.RESERVE_CONFIRM_FAIL),
                0
            );
    }

    @Override
    public void afterChunkError(ChunkContext context) {

    }
}
