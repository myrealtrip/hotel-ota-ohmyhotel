package com.myrealtrip.ohmyhotel.core.infrastructure.hotel.querydsl;

import com.myrealtrip.ohmyhotel.core.domain.hotel.dto.HotelModifyInfo;
import com.myrealtrip.ohmyhotel.core.domain.hotel.entity.HotelEntity;

import java.util.List;

public interface HotelCustomRepository {

    List<HotelModifyInfo> findHotelModifyInfoByHotelIds(List<Long> hotelIds);

    List<HotelEntity> findByHotelIds(List<Long> hotelIds);
}
