package com.myrealtrip.ohmyhotel.core.infrastructure.reservation;

import com.myrealtrip.ohmyhotel.core.domain.reservation.entity.ReservationApiLogEntity;
import com.myrealtrip.ohmyhotel.core.infrastructure.reservation.querydsl.ReservationApiLogCustomRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationApiLogRepository extends JpaRepository<ReservationApiLogEntity, Long>, ReservationApiLogCustomRepository {
}
