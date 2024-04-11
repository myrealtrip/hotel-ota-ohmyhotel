package com.myrealtrip.ohmyhotel.core.infrastructure.zeromargin;

import com.myrealtrip.ohmyhotel.core.domain.zeromargin.entity.GlobalZeroMarginEntity;
import com.myrealtrip.ohmyhotel.core.infrastructure.zeromargin.querydsl.GlobalZeroMarginCustomRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GlobalZeroMarginRepository extends JpaRepository<GlobalZeroMarginEntity, Long>, GlobalZeroMarginCustomRepository {
}
