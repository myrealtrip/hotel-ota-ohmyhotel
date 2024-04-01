package com.myrealtrip.ohmyhotel.core.infrastructure.reservation;

import com.myrealtrip.ohmyhotel.core.domain.reservation.entity.ReservationEntity;
import com.myrealtrip.ohmyhotel.core.infrastructure.reservation.querydsl.ReservationCustomRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationRepository extends JpaRepository<ReservationEntity, Long>, ReservationCustomRepository {
}
