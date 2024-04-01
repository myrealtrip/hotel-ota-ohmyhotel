package com.myrealtrip.ohmyhotel.core.provider.reservation;

import com.myrealtrip.ohmyhotel.core.domain.reservation.dto.Order;
import com.myrealtrip.ohmyhotel.core.domain.reservation.entity.OrderEntity;
import com.myrealtrip.ohmyhotel.core.infrastructure.reservation.OrderRepository;
import com.myrealtrip.ohmyhotel.core.provider.reservation.mapper.OrderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class OrderProvider {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    @Transactional
    public void upsert(Order order) {
        OrderEntity entity = orderMapper.toEntity(order);
        orderRepository.save(entity);
    }
}
