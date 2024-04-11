package com.myrealtrip.ohmyhotel.core.infrastructure.zeromargin;

import com.myrealtrip.ohmyhotel.core.domain.zeromargin.entity.CustomZeroMarginEntity;
import com.myrealtrip.ohmyhotel.core.infrastructure.zeromargin.querydsl.CustomZeroMarginCustomRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomZeroMarginRepository extends JpaRepository<CustomZeroMarginEntity, Long>, CustomZeroMarginCustomRepository {
}
