package com.myrealtrip.ohmyhotel.core.provider.hotel;

import com.myrealtrip.ohmyhotel.core.domain.hotel.dto.Hotel;
import com.myrealtrip.ohmyhotel.core.infrastructure.hotel.HotelRepository;
import com.myrealtrip.ohmyhotel.core.provider.hotel.mapper.HotelMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HotelProvider {

    private final HotelRepository hotelRepository;
    private final HotelMapper hotelMapper;
}
