package com.myrealtrip.ohmyhotel.batch.reader;

import com.myrealtrip.ohmyhotel.core.domain.reservation.dto.Reservation;
import com.myrealtrip.ohmyhotel.core.provider.reservation.ReservationProvider;
import com.myrealtrip.ohmyhotel.enumarate.ReservationStatus;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.batch.item.database.AbstractPagingItemReader;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class ReservationReader extends AbstractPagingItemReader<Reservation> {

    private final ReservationProvider reservationProvider;
    private final ReservationStatus status;
    private Long lastSelectId = 0L;

    public ReservationReader(ReservationProvider reservationProvider, ReservationStatus status, int pageSize) {
        this.reservationProvider = reservationProvider;
        this.status = status;
        super.setPageSize(pageSize);
    }

    @Override
    protected void doReadPage() {
        log.info("page {}, read", super.getPage());

        super.results = reservationProvider.getByReservationIdGreaterThanAndStatus(lastSelectId, status, getPageSize());
        if (CollectionUtils.isNotEmpty(super.results)) {
            lastSelectId = super.results.get(super.results.size() - 1).getReservationId();
        }
    }

    @Override
    protected void doJumpToPage(int itemIndex) {

    }
}
