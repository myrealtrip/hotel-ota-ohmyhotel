package com.myrealtrip.ohmyhotel.core.infrastructure.reservation.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ReservationApiLogCustomRepositoryImpl implements ReservationApiLogCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;
}
