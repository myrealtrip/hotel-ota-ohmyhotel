package com.myrealtrip.ohmyhotel.core.infrastructure.zeromargin.querydsl;

import com.myrealtrip.ohmyhotel.core.domain.zeromargin.entity.CustomZeroMarginEntity;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.myrealtrip.ohmyhotel.core.domain.zeromargin.entity.QCustomZeroMarginEntity.customZeroMarginEntity;

@RequiredArgsConstructor
@Repository
public class CustomZeroMarginCustomRepositoryImpl implements CustomZeroMarginCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<CustomZeroMarginEntity> findAllByHotelIds(List<Long> hotelIds) {
        return jpaQueryFactory.selectFrom(customZeroMarginEntity)
            .where(customZeroMarginEntity.hotelId.in(hotelIds),
                   customZeroMarginEntity.deletedAt.isNull())
            .fetch();
    }
}
