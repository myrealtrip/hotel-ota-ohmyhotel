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
    public Order upsert(Order order) {
        OrderEntity entity = orderMapper.toEntity(order);
        return orderMapper.toDto(orderRepository.save(entity));
    }

    @Transactional
    public Order getByOrderId(Long orderId) {
        return orderMapper.toDto(orderRepository.findByOrderId(orderId));
    }
}
