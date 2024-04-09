package com.myrealtrip.ohmyhotel.core.provider.hotel;

import com.myrealtrip.ohmyhotel.core.domain.hotel.dto.Hotel;
import com.myrealtrip.ohmyhotel.core.domain.hotel.dto.HotelModifyInfo;
import com.myrealtrip.ohmyhotel.core.domain.hotel.entity.HotelEntity;
import com.myrealtrip.ohmyhotel.core.infrastructure.hotel.HotelRepository;
import com.myrealtrip.ohmyhotel.core.provider.hotel.mapper.HotelMapper;
import com.myrealtrip.ohmyhotel.enumarate.HotelStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class HotelProvider {

    private final HotelRepository hotelRepository;
    private final HotelMapper hotelMapper;

    @Transactional
    public void upsert(List<Hotel> hotels) {
        List<HotelEntity> entities = hotels.stream()
            .map(hotelMapper::toEntity)
            .collect(Collectors.toList());

        hotelRepository.saveAll(entities);
    }

    @Transactional
    public List<HotelModifyInfo> getHotelModifyInfoByHotelIds(List<Long> hotelIds) {
        return hotelRepository.findHotelModifyInfoByHotelIds(hotelIds);
    }

    @Transactional
    public List<Hotel> getByHotelIds(List<Long> hotelIds) {
        return hotelRepository.findByHotelIds(hotelIds)
            .stream()
            .map(hotelMapper::toDto)
            .collect(Collectors.toList());
    }

    @Transactional
    public void updateStatusByHotelIds(List<Long> hotelIds, HotelStatus status) {
        List<HotelEntity> entities = hotelRepository.findAllById(hotelIds);
        for (HotelEntity entity : entities) {
            entity.updateStatus(status);
        }
        hotelRepository.saveAll(entities);
    }

    @Transactional(readOnly = true)
    public List<Hotel> getByHotelIdGreaterThan(Long hotelId, int limit) {
        return hotelRepository.findAllByHotelIdGreaterThan(hotelId, limit)
            .stream()
            .map(hotelMapper::toDto)
            .collect(Collectors.toList());
    }
}
