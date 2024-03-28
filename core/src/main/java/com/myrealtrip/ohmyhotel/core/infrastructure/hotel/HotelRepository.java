package com.myrealtrip.ohmyhotel.core.infrastructure.hotel;

import com.myrealtrip.ohmyhotel.core.domain.hotel.entity.HotelEntity;
import com.myrealtrip.ohmyhotel.core.infrastructure.hotel.querydsl.HotelCustomRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HotelRepository extends JpaRepository<HotelEntity, Long>, HotelCustomRepository {
}
