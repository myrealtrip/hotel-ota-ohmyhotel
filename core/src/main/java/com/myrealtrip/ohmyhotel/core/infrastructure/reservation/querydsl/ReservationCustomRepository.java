package com.myrealtrip.ohmyhotel.core.infrastructure.reservation.querydsl;

import com.myrealtrip.ohmyhotel.core.domain.reservation.entity.ReservationEntity;

public interface ReservationCustomRepository {

    ReservationEntity findByMrtReservationNo(String mrtReservationNo);

    ReservationEntity findByMrtReservationNoWithLock(String mrtReservationNo);
}
