package com.myrealtrip.ohmyhotel.core.provider.reservation.mapper;

import com.myrealtrip.ohmyhotel.core.domain.reservation.dto.ReservationApiLog;
import com.myrealtrip.ohmyhotel.core.domain.reservation.entity.ReservationApiLogEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ReservationApiLogMapper {

    ReservationApiLog toDto(ReservationApiLogEntity entity);

    ReservationApiLogEntity toEntity(ReservationApiLog dto);
}
