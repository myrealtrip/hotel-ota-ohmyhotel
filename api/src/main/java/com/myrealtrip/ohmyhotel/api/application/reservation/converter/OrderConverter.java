package com.myrealtrip.ohmyhotel.api.application.reservation.converter;

import com.myrealtrip.ohmyhotel.core.domain.hotel.dto.Hotel;
import com.myrealtrip.ohmyhotel.core.domain.reservation.dto.AdditionalOrderInfo;
import com.myrealtrip.ohmyhotel.core.domain.reservation.dto.GuestCount;
import com.myrealtrip.ohmyhotel.core.domain.reservation.dto.Order;
import com.myrealtrip.ohmyhotel.core.domain.zeromargin.dto.ZeroMargin;
import com.myrealtrip.ohmyhotel.core.provider.hotel.HotelProvider;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.avilability.protocol.response.OmhRoomsAvailabilityResponse.OmhRoomAvailability;
import com.myrealtrip.ohmyhotel.utils.OmhPriceCalculateUtils;
import com.myrealtrip.srtcommon.support.utils.ZeroMarginUtils;
import com.myrealtrip.unionstay.dto.hotelota.search.request.BaseSearchRequest;
import com.myrealtrip.unionstay.dto.hotelota.search.request.ReservationSearchRequest;
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

    public Order toOrder(ReservationSearchRequest searchRequest,
                         OmhRoomAvailability omhRoomAvailability,
                         BigDecimal mrtCommissionRate,
                         ZeroMargin zeroMargin) {
        Long hotelId = Long.valueOf(searchRequest.getPropertyId());
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
            .zeroMarginApply(zeroMargin.isOn())
            .zeroMarginApplyPrice(zeroMargin.isOn() ?
                                  ZeroMarginUtils.toZeroMarginSalePrice(omhRoomAvailability.getTotalNetAmount(), zeroMargin.getZeroMarginRate()) :
                                  null)
            .mrtCommissionRate(mrtCommissionRate)
            .guestCount(GuestCount.builder()
                            .adultCount(searchRequest.getAdultCount())
                            .childCount(searchRequest.getChildCount())
                            .childAges(searchRequest.getChildAges())
                            .build())
            .additionalInfo(AdditionalOrderInfo.builder()
                                .rateType(omhRoomAvailability.getRateType())
                                .roomToken(omhRoomAvailability.getRoomToken())
                                .cancelPolicy(omhRoomAvailability.getCancellationPolicy())
                                .bedGroups(omhRoomAvailability.getBedGroups())
                                .mealBasisCode(omhRoomAvailability.getMealBasisCode())
                                .nightlyAmounts(omhRoomAvailability.getNightly())
                                .build())
            .build();
    }
}
