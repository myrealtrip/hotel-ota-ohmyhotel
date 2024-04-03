package com.myrealtrip.ohmyhotel.core.infrastructure.hotel.querydsl;

import com.myrealtrip.ohmyhotel.core.domain.hotel.dto.HotelModifyInfo;
import com.myrealtrip.ohmyhotel.core.domain.hotel.entity.HotelEntity;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import static com.myrealtrip.ohmyhotel.core.domain.hotel.entity.QHotelEntity.*;

import java.util.List;

@Repository
@RequiredArgsConstructor
@Slf4j
public class HotelCustomRepositoryImpl implements HotelCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<HotelModifyInfo> findHotelModifyInfoByHotelIds(List<Long> hotelIds) {
        return jpaQueryFactory.from(hotelEntity)
            .select(Projections.constructor(HotelModifyInfo.class,
                                            hotelEntity.hotelId,
                                            hotelEntity.createdAt,
                                            hotelEntity.createdBy,
                                            hotelEntity.updatedAt,
                                            hotelEntity.updatedBy,
                                            hotelEntity.deletedAt,
                                            hotelEntity.deletedBy))
            .where(hotelEntity.hotelId.in(hotelIds),
                   hotelEntity.deletedAt.isNull())
            .fetch();
    }

    @Override
    public List<HotelEntity> findByHotelIds(List<Long> hotelIds) {
        return jpaQueryFactory.selectFrom(hotelEntity)
            .where(hotelEntity.hotelId.in(hotelIds),
                   hotelEntity.deletedAt.isNull())
            .fetch();
    }

    @Override
    public List<Long> getAllHotelIds() {
        return jpaQueryFactory.from(hotelEntity)
            .select(hotelEntity.hotelId)
            .where(hotelEntity.deletedAt.isNull())
            .fetch();
    }
}
