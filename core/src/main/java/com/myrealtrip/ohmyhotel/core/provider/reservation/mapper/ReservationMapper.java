package com.myrealtrip.ohmyhotel.core.provider.reservation.mapper;

import com.myrealtrip.ohmyhotel.core.domain.reservation.dto.Reservation;
import com.myrealtrip.ohmyhotel.core.domain.reservation.entity.ReservationEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ReservationMapper {

    Reservation toDto(ReservationEntity entity);

    ReservationEntity toEntity(Reservation dto);
}
