package com.myrealtrip.ohmyhotel.api.application.reservation.converter;

import com.myrealtrip.ohmyhotel.core.domain.reservation.dto.Order;
import com.myrealtrip.ohmyhotel.enumarate.Language;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.avilability.protocol.response.OmhRoomsAvailabilityResponse.OmhRoomAvailability;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.avilability.protocol.request.OmhRoomGuestCount;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.reservation.protocol.request.OmhPreCheckRequest;
import com.myrealtrip.unionstay.dto.hotelota.precheck.request.PreCheckRequest;
import com.myrealtrip.unionstay.dto.hotelota.search.request.ReservationSearchRequest;
import com.myrealtrip.unionstay.dto.hotelota.search.request.SearchRequest;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PreCheckRequestConverter {

    public OmhPreCheckRequest toOmhPreCheckRequest(ReservationSearchRequest searchRequest, OmhRoomAvailability omhRoomAvailability) {
        return OmhPreCheckRequest.builder()
            .language(Language.KO)
            .hotelCode(Long.valueOf(searchRequest.getPropertyId()))
            .checkInDate(searchRequest.getCheckin())
            .checkOutDate(searchRequest.getCheckout())
            .roomTypeCode(omhRoomAvailability.getRoomTypeCode())
            .roomToken(omhRoomAvailability.getRoomToken())
            .ratePlanCode(omhRoomAvailability.getRatePlanCode())
            .rateType(omhRoomAvailability.getRateType())
            .totalNetAmount(omhRoomAvailability.getTotalNetAmount())
            .rooms(List.of(toOmhRoomGuestCount(searchRequest.getAdultCount(), searchRequest.getChildCount(), searchRequest.getChildAges())))
            .build();
    }

    public OmhPreCheckRequest toOmhPreCheckRequest(PreCheckRequest preCheckRequest, Order order) {
        return OmhPreCheckRequest.builder()
            .language(Language.KO)
            .hotelCode(Long.valueOf(preCheckRequest.getPropertyId()))
            .checkInDate(preCheckRequest.getCheckin())
            .checkOutDate(preCheckRequest.getCheckout())
            .roomTypeCode(preCheckRequest.getRoomId())
            .roomToken(order.getAdditionalInfo().getRoomToken())
            .ratePlanCode(preCheckRequest.getRateId())
            .rateType(order.getAdditionalInfo().getRateType())
            .totalNetAmount(order.getDepositPrice())
            .rooms(List.of(toOmhRoomGuestCount(preCheckRequest.getAdultCount(), preCheckRequest.getChildCount(), preCheckRequest.getChildAges())))
            .build();
    }

    private OmhRoomGuestCount toOmhRoomGuestCount(Integer adultCount, Integer childCount, List<Integer> childAges) {
        return OmhRoomGuestCount.builder()
            .adultCount(adultCount)
            .childCount(childCount)
            .childAges(childAges)
            .build();
    }
}
