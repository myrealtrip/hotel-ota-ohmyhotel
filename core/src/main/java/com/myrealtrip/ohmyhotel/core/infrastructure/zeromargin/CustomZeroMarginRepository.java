package com.myrealtrip.ohmyhotel.core.infrastructure.zeromargin;

import com.myrealtrip.ohmyhotel.core.domain.zeromargin.entity.CustomZeroMarginEntity;
import com.myrealtrip.ohmyhotel.core.infrastructure.zeromargin.querydsl.CustomZeroMarginCustomRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CustomZeroMarginRepository extends JpaRepository<CustomZeroMarginEntity, Long>, CustomZeroMarginCustomRepository {

    List<CustomZeroMarginEntity> findAllByHotelIdInAndDeletedAtIsNull(List<Long> hotelIds);
}
