package com.myrealtrip.ohmyhotel.core.infrastructure.reservation.querydsl;

import com.myrealtrip.ohmyhotel.core.domain.reservation.entity.ReservationEntity;
import com.myrealtrip.ohmyhotel.enumarate.ReservationStatus;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import java.time.LocalDate;
import java.util.List;
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

    @Override
    public List<ReservationEntity> findByReservationIdGtAndStatus(Long reservationId, ReservationStatus status, int limit) {
        return jpaQueryFactory.selectFrom(reservationEntity)
            .where(reservationEntity.reservationId.gt(reservationId),
                   reservationEntity.reservationStatus.eq(status),
                   reservationEntity.deletedAt.isNull())
            .orderBy(reservationEntity.reservationId.asc())
            .limit(limit)
            .fetch();
    }

    @Override
    public List<ReservationEntity> findByReservationIdGtAndCheckInDateGoeAndStatus(Long reservationId, LocalDate checkInDate, ReservationStatus status, int limit) {
        return jpaQueryFactory.selectFrom(reservationEntity)
            .where(reservationEntity.reservationId.gt(reservationId),
                   reservationEntity.checkInDate.goe(checkInDate),
                   reservationEntity.reservationStatus.eq(status),
                   reservationEntity.deletedAt.isNull())
            .orderBy(reservationEntity.reservationId.asc())
            .limit(limit)
            .fetch();
    }
}
