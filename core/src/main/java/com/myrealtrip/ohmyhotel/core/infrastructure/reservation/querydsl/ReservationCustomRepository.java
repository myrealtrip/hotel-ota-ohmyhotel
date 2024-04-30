package com.myrealtrip.ohmyhotel.core.infrastructure.reservation.querydsl;

import com.myrealtrip.ohmyhotel.core.domain.reservation.entity.ReservationEntity;
import com.myrealtrip.ohmyhotel.enumarate.ReservationStatus;

import java.util.List;

public interface ReservationCustomRepository {

    ReservationEntity findByMrtReservationNo(String mrtReservationNo);

    ReservationEntity findByMrtReservationNoWithLock(String mrtReservationNo);

    List<ReservationEntity> findByReservationIdGreaterThanAndStatus(Long reservationId, ReservationStatus status, int limit);
}
