package com.myrealtrip.ohmyhotel.core.infrastructure.reservation.querydsl;

import com.myrealtrip.ohmyhotel.core.domain.reservation.entity.OrderEntity;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static com.myrealtrip.ohmyhotel.core.domain.reservation.entity.QOrderEntity.orderEntity;

@Repository
@RequiredArgsConstructor
public class OrderCustomRepositoryImpl implements OrderCustomRepository{

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public OrderEntity findByOrderId(Long orderId) {
        return jpaQueryFactory.selectFrom(orderEntity)
            .where(orderEntity.orderId.eq(orderId),
                   orderEntity.deletedAt.isNull())
            .fetchOne();
    }
}
