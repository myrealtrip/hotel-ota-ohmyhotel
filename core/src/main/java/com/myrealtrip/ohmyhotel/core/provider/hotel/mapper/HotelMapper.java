package com.myrealtrip.ohmyhotel.core.provider.hotel.mapper;

import com.myrealtrip.ohmyhotel.core.domain.hotel.dto.Hotel;
import com.myrealtrip.ohmyhotel.core.domain.hotel.entity.HotelEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface HotelMapper {

    Hotel toDto(HotelEntity entity);

    HotelEntity toEntity(Hotel dto);
}
