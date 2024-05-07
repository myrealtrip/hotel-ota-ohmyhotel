package com.myrealtrip.ohmyhotel.batch.reader;

import com.myrealtrip.ohmyhotel.core.domain.reservation.dto.Reservation;
import com.myrealtrip.ohmyhotel.core.provider.reservation.ReservationProvider;
import com.myrealtrip.ohmyhotel.enumarate.ReservationStatus;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.batch.item.database.AbstractPagingItemReader;

import java.time.LocalDate;

/**
 * 확정상태 이면서 아직 체크인 하지 않은 에약건을 읽어온다.
 */
@Slf4j
public class ConfirmAndNotCheckInReservationReader extends AbstractPagingItemReader<Reservation> {

    private final LocalDate now = LocalDate.now();
    private final ReservationProvider reservationProvider;
    private Long lastSelectId = 0L;

    public ConfirmAndNotCheckInReservationReader(ReservationProvider reservationProvider,
                                                 int pageSize) {
        this.reservationProvider = reservationProvider;
        super.setPageSize(pageSize);
    }

    @Override
    protected void doReadPage() {
        log.info("page {}, read", super.getPage());

        super.results = reservationProvider.getByReservationIdGtAndCheckInDateGoeAndStatus(lastSelectId, now, ReservationStatus.RESERVE_CONFIRM, getPageSize());
        if (CollectionUtils.isNotEmpty(super.results)) {
            lastSelectId = super.results.get(super.results.size() - 1).getReservationId();
        }
    }

    @Override
    protected void doJumpToPage(int itemIndex) {

    }
}
