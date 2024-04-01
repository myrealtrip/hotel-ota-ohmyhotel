package com.myrealtrip.ohmyhotel.core.infrastructure.reservation;

import com.myrealtrip.ohmyhotel.core.domain.reservation.entity.OrderEntity;
import com.myrealtrip.ohmyhotel.core.infrastructure.reservation.querydsl.OrderCustomRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long>, OrderCustomRepository {
}
