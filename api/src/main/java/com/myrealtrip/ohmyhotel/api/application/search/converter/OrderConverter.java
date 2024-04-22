package com.myrealtrip.ohmyhotel.api.application.search.converter;

import com.myrealtrip.ohmyhotel.core.domain.hotel.dto.Hotel;
import com.myrealtrip.ohmyhotel.core.domain.reservation.dto.AdditionalOrderInfo;
import com.myrealtrip.ohmyhotel.core.domain.reservation.dto.GuestCount;
import com.myrealtrip.ohmyhotel.core.domain.reservation.dto.Order;
import com.myrealtrip.ohmyhotel.core.provider.hotel.HotelProvider;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.avilability.protocol.OmhRoomsAvailabilityResponse;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.avilability.protocol.OmhRoomsAvailabilityResponse.OmhRoomAvailability;
import com.myrealtrip.ohmyhotel.utils.OmhPriceCalculateUtils;
import com.myrealtrip.unionstay.dto.hotelota.search.request.SearchRequest;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderConverter {

    private final HotelProvider hotelProvider;

    public Order toOrder(SearchRequest searchRequest,
                         OmhRoomAvailability omhRoomAvailability,
                         BigDecimal mrtCommissionRate) {
        Long hotelId = Long.valueOf(searchRequest.getPropertyIds().get(0));
        Hotel hotel = hotelProvider.getByHotelIds(List.of(hotelId)).get(0);
        return Order.builder()
            .hotelId(hotelId)
            .hotelName(hotel.getKoName())
            .roomTypeCode(omhRoomAvailability.getRoomTypeCode())
            .roomTypeName(StringUtils.isNotBlank(omhRoomAvailability.getRoomTypeNameByLanguage()) ?
                          omhRoomAvailability.getRoomTypeNameByLanguage() :
                          omhRoomAvailability.getRoomTypeName())
            .ratePlanCode(omhRoomAvailability.getRatePlanCode())
            .ratePlanName(StringUtils.isNotBlank(omhRoomAvailability.getRatePlanNameByLanguage()) ?
                          omhRoomAvailability.getRatePlanNameByLanguage() :
                          omhRoomAvailability.getRatePlanName())
            .checkInDate(searchRequest.getCheckin())
            .checkOutDate(searchRequest.getCheckout())
            .salePrice(OmhPriceCalculateUtils.toSalePrice(omhRoomAvailability.getTotalNetAmount(), mrtCommissionRate))
            .depositPrice(omhRoomAvailability.getTotalNetAmount())
            .zeroMarginApply(false)
            .zeroMarginApplyPrice(null)
            .mrtCommissionRate(mrtCommissionRate)
            .guestCount(GuestCount.builder()
                            .adultCount(searchRequest.getAdultCount())
                            .childCount(searchRequest.getChildCount())
                            .childAges(searchRequest.getChildAges())
                            .build())
            .additionalInfo(AdditionalOrderInfo.builder()
                                .rateType(omhRoomAvailability.getRateType())
                                .build())
            .build();
    }
}
