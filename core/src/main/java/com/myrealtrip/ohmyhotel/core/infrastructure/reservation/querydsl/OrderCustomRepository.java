package com.myrealtrip.ohmyhotel.core.infrastructure.reservation.querydsl;

import com.myrealtrip.ohmyhotel.core.domain.reservation.entity.OrderEntity;

public interface OrderCustomRepository {

    OrderEntity findByOrderId(Long orderId);
}
