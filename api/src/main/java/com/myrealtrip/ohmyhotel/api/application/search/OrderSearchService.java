package com.myrealtrip.ohmyhotel.api.application.search;


import com.myrealtrip.ohmyhotel.api.application.common.ReservationApiLogService;
import com.myrealtrip.ohmyhotel.api.application.search.converter.OrderConverter;
import com.myrealtrip.ohmyhotel.api.application.search.converter.SearchRequestConverter;
import com.myrealtrip.ohmyhotel.api.application.search.converter.SingleSearchResponseConverter;
import com.myrealtrip.ohmyhotel.api.protocol.search.RateSearchId;
import com.myrealtrip.ohmyhotel.core.domain.reservation.dto.Order;
import com.myrealtrip.ohmyhotel.core.domain.zeromargin.dto.ZeroMargin;
import com.myrealtrip.ohmyhotel.core.provider.reservation.OrderProvider;
import com.myrealtrip.ohmyhotel.core.service.CommissionRateService;
import com.myrealtrip.ohmyhotel.core.service.ZeroMarginSearchService;
import com.myrealtrip.ohmyhotel.enumarate.ApiLogType;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.avilability.OmhRoomsAvailabilityAgent;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.avilability.protocol.OmhRoomsAvailabilityResponse;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.avilability.protocol.OmhRoomsAvailabilityResponse.OmhRoomAvailability;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.avilability.protocol.request.OmhRoomsAvailabilityRequest;
import com.myrealtrip.srtcommon.support.utils.ObjectMapperUtils;
import com.myrealtrip.unionstay.dto.hotelota.search.request.SearchRequest;
import com.myrealtrip.unionstay.dto.hotelota.search.response.SearchResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static java.util.Objects.isNull;

@Service
@RequiredArgsConstructor
public class OrderSearchService {

    private final ReservationApiLogService reservationApiLogService;
    private final ZeroMarginSearchService zeroMarginSearchService;
    private final CommissionRateService commissionRateService;
    private final OrderProvider orderProvider;

    private final OmhRoomsAvailabilityAgent omhRoomsAvailabilityAgent;

    private final SingleSearchResponseConverter singleSearchResponseConverter;
    private final SearchRequestConverter searchRequestConverter;
    private final OrderConverter orderConverter;

    /**
     * 숙소의 실시간 재고/가격을 검색합니다. (주문서 진입시 호출)
     *
     * @param searchRequest
     * @return
     */
    @Transactional
    public SearchResponse search(SearchRequest searchRequest) {
        if (searchRequest.getPropertyIds().size() != 1) {
            throw new IllegalArgumentException("1개의 숙소만 검색할 수 있습니다.");
        }
        Long hotelId = Long.valueOf(searchRequest.getPropertyIds().get(0));
        RateSearchId rateSearchId = RateSearchId.from(searchRequest.getRateSearchId());
        OmhRoomsAvailabilityRequest omhRoomsAvailabilityRequest = searchRequestConverter.toOmhRoomsAvailabilityRequest(searchRequest);
        OmhRoomsAvailabilityResponse omhRoomsAvailabilityResponse = omhRoomsAvailabilityAgent.getRoomsAvailability(omhRoomsAvailabilityRequest);
        OmhRoomAvailability orderedRoomAvailability = getOrderedOmhRoomAvailability(omhRoomsAvailabilityResponse, rateSearchId);
        if (isNull(orderedRoomAvailability)) {
            return singleSearchResponseConverter.empty();
        }

        BigDecimal mrtCommissionRate = commissionRateService.getMrtCommissionRate();
        ZeroMargin zeroMargin = zeroMarginSearchService.getZeroMargin(hotelId, true);
        Order order = saveOrder(searchRequest, mrtCommissionRate, orderedRoomAvailability, zeroMargin);
        saveApiLog(order.getOrderId(), omhRoomsAvailabilityRequest, omhRoomsAvailabilityResponse);
        return singleSearchResponseConverter.toSearchResponse(
            hotelId,
            orderedRoomAvailability,
            mrtCommissionRate,
            searchRequest.getRatePlanCount(),
            zeroMargin
        );
    }

    private OmhRoomAvailability getOrderedOmhRoomAvailability(OmhRoomsAvailabilityResponse omhRoomsAvailabilityResponse,
                                                              RateSearchId rateSearchId) {
        for (OmhRoomAvailability omhRoomAvailability : omhRoomsAvailabilityResponse.getRooms()) {
            if (omhRoomAvailability.getRoomTypeCode().equals(rateSearchId.getRoomTypeCode()) &&
                omhRoomAvailability.getRatePlanCode().equals(rateSearchId.getRatePlanCode())) {
                return omhRoomAvailability;
            }
        }
        return null;
    }

    private Order saveOrder(SearchRequest searchRequest, BigDecimal mrtCommissionRate, OmhRoomAvailability omhRoomAvailability, ZeroMargin zeroMargin) {
        Order order = orderConverter.toOrder(searchRequest, omhRoomAvailability, mrtCommissionRate, zeroMargin);
        return orderProvider.upsert(order);
    }

    private void saveApiLog(Long orderId,
                            OmhRoomsAvailabilityRequest omhRoomsAvailabilityRequest,
                            OmhRoomsAvailabilityResponse omhRoomsAvailabilityResponse) {
        reservationApiLogService.upsertRoomsAvailabilityLog(orderId, ApiLogType.REQUEST, ObjectMapperUtils.writeAsString(omhRoomsAvailabilityRequest));
        reservationApiLogService.upsertRoomsAvailabilityLog(orderId, ApiLogType.RESPONSE, ObjectMapperUtils.writeAsString(omhRoomsAvailabilityResponse));
    }
}
