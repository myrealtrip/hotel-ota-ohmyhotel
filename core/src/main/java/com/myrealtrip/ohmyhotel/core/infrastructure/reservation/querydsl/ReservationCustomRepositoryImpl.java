package com.myrealtrip.ohmyhotel.core.infrastructure.reservation.querydsl;

import com.myrealtrip.ohmyhotel.core.domain.reservation.entity.ReservationEntity;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import java.util.Optional;

import static com.myrealtrip.ohmyhotel.core.domain.reservation.entity.QReservationEntity.reservationEntity;

@Repository
@RequiredArgsConstructor
public class ReservationCustomRepositoryImpl implements ReservationCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public ReservationEntity findByMrtReservationNo(String mrtReservationNo) {
        return jpaQueryFactory.selectFrom(reservationEntity)
            .where(reservationEntity.mrtReservationNo.eq(mrtReservationNo),
                   reservationEntity.deletedAt.isNull())
            .fetchOne();
    }

    public ReservationEntity findByMrtReservationNoWithLock(String mrtReservationNo) {
        return jpaQueryFactory.selectFrom(reservationEntity)
            .where(reservationEntity.mrtReservationNo.eq(mrtReservationNo))
            .setLockMode(LockModeType.PESSIMISTIC_WRITE)
            .setHint("javax.persistence.query.timeout", 15000)
            .fetchOne();

    }
}
