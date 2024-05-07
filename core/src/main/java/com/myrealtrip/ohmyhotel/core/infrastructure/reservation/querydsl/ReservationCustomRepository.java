package com.myrealtrip.ohmyhotel.core.infrastructure.reservation.querydsl;

import com.myrealtrip.ohmyhotel.core.domain.reservation.entity.ReservationEntity;
import com.myrealtrip.ohmyhotel.enumarate.ReservationStatus;

import java.time.LocalDate;
import java.util.List;

public interface ReservationCustomRepository {

    ReservationEntity findByMrtReservationNo(String mrtReservationNo);

    ReservationEntity findByMrtReservationNoWithLock(String mrtReservationNo);

    List<ReservationEntity> findByReservationIdGtAndStatus(Long reservationId, ReservationStatus status, int limit);

    List<ReservationEntity> findByReservationIdGtAndCheckInDateGoeAndStatus(Long reservationId, LocalDate checkInDate, ReservationStatus status, int limit);
}
