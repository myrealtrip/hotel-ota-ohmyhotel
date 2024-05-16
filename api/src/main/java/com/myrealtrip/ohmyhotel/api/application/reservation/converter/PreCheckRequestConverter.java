package com.myrealtrip.ohmyhotel.api.application.reservation.converter;

import com.myrealtrip.ohmyhotel.core.domain.reservation.dto.Order;
import com.myrealtrip.ohmyhotel.enumarate.Language;
import com.myrealtrip.ohmyhotel.enumarate.RateType;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.avilability.protocol.request.OmhRoomGuestCount;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.reservation.protocol.request.OmhPreCheckRequest;
import com.myrealtrip.unionstay.dto.hotelota.precheck.request.PreCheckRequest;
import com.myrealtrip.unionstay.dto.hotelota.search.request.SearchRequest;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class PreCheckRequestConverter {

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
            .rooms(List.of(toOmhRoomGuestCount(preCheckRequest)))
            .build();
    }

    private OmhRoomGuestCount toOmhRoomGuestCount(PreCheckRequest preCheckRequest) {
        return OmhRoomGuestCount.builder()
            .adultCount(preCheckRequest.getAdultCount())
            .childCount(preCheckRequest.getChildCount())
            .childAges(preCheckRequest.getChildAges())
            .build();
    }
}
