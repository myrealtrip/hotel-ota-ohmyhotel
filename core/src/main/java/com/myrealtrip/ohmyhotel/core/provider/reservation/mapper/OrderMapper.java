package com.myrealtrip.ohmyhotel.core.provider.reservation.mapper;

import com.myrealtrip.ohmyhotel.core.domain.reservation.dto.Order;
import com.myrealtrip.ohmyhotel.core.domain.reservation.entity.OrderEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    Order toDto(OrderEntity entity);

    OrderEntity toEntity(Order order);
}
