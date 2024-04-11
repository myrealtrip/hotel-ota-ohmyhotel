package com.myrealtrip.ohmyhotel.core.infrastructure.zeromargin.querydsl;

import com.myrealtrip.ohmyhotel.core.domain.zeromargin.entity.CustomZeroMarginEntity;

import java.util.List;

public interface CustomZeroMarginCustomRepository {

    List<CustomZeroMarginEntity> findAllByHotelIds(List<Long> hotelIds);
}
