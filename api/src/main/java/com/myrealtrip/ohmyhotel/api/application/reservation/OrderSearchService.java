package com.myrealtrip.ohmyhotel.api.application.reservation;


import com.myrealtrip.ohmyhotel.api.application.reservation.converter.PreCheckRequestConverter;
import com.myrealtrip.ohmyhotel.core.domain.partner.dto.MrtCommissionInfo;
import com.myrealtrip.ohmyhotel.core.service.reservation.ReservationApiLogService;
import com.myrealtrip.ohmyhotel.api.application.reservation.converter.OrderConverter;
import com.myrealtrip.ohmyhotel.api.application.common.converter.SearchRequestConverter;
import com.myrealtrip.ohmyhotel.api.application.common.converter.SingleSearchResponseConverter;
import com.myrealtrip.ohmyhotel.api.protocol.search.RateSearchId;
import com.myrealtrip.ohmyhotel.core.domain.reservation.dto.Order;
import com.myrealtrip.ohmyhotel.core.domain.zeromargin.dto.ZeroMargin;
import com.myrealtrip.ohmyhotel.core.provider.reservation.OrderProvider;
import com.myrealtrip.ohmyhotel.core.service.CommissionRateService;
import com.myrealtrip.ohmyhotel.core.service.ZeroMarginSearchService;
import com.myrealtrip.ohmyhotel.enumarate.ApiLogType;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.avilability.OmhRoomsAvailabilityAgent;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.avilability.protocol.response.OmhRoomsAvailabilityResponse;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.avilability.protocol.response.OmhRoomsAvailabilityResponse.OmhRoomAvailability;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.avilability.protocol.request.OmhRoomsAvailabilityRequest;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.reservation.OmhPreCheckAgent;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.reservation.protocol.request.OmhPreCheckRequest;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.reservation.protocol.response.OmhPreCheckResponse;
import com.myrealtrip.srtcommon.support.utils.ObjectMapperUtils;
import com.myrealtrip.unionstay.dto.hotelota.search.request.BaseSearchRequest;
import com.myrealtrip.unionstay.dto.hotelota.search.request.ReservationSearchRequest;
import com.myrealtrip.unionstay.dto.hotelota.search.request.SearchRequest;
import com.myrealtrip.unionstay.dto.hotelota.search.response.SearchResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
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

    private final OmhPreCheckAgent omhPreCheckAgent;
    private final PreCheckRequestConverter preCheckRequestConverter;

    /**
     * 숙소의 실시간 재고/가격을 검색합니다. (주문서 진입시 호출)
     *
     * @param reservationSearchRequest
     * @return
     */
    @Transactional
    public SearchResponse search(ReservationSearchRequest reservationSearchRequest) {
        if (StringUtils.isBlank(reservationSearchRequest.getPropertyId())) {
            throw new IllegalArgumentException("숙소가 없습니다.");
        }
        Long hotelId = Long.valueOf(reservationSearchRequest.getPropertyId());
        RateSearchId rateSearchId = RateSearchId.from(reservationSearchRequest.getRateSearchId());
        OmhRoomsAvailabilityRequest omhRoomsAvailabilityRequest = searchRequestConverter.toOmhRoomsAvailabilityRequest(reservationSearchRequest);
        OmhRoomsAvailabilityResponse omhRoomsAvailabilityResponse = omhRoomsAvailabilityAgent.getRoomsAvailability(omhRoomsAvailabilityRequest);
        OmhRoomAvailability orderedRoomAvailability = getOrderedOmhRoomAvailability(omhRoomsAvailabilityResponse, rateSearchId);
        if (isNull(orderedRoomAvailability)) {
            return singleSearchResponseConverter.empty();
        }

        // pre-check API 호출하여 최종 가격 으로 업데이트
        OmhPreCheckRequest omhPreCheckRequest = preCheckRequestConverter.toOmhPreCheckRequest(reservationSearchRequest, orderedRoomAvailability);
        OmhPreCheckResponse omhPreCheckResponse = omhPreCheckAgent.preCheck(omhPreCheckRequest);
        orderedRoomAvailability.changeTotalNetAmount(omhPreCheckResponse.getAmount().getTotalNetAmount());

        MrtCommissionInfo mrtCommissionInfo = commissionRateService.getMrtCommissionInfo();
        ZeroMargin zeroMargin = zeroMarginSearchService.getZeroMargin(hotelId, true);
        Order order = saveOrder(reservationSearchRequest, mrtCommissionInfo.getCommissionRate(), orderedRoomAvailability, zeroMargin);
        saveApiLog(order.getOrderId(), omhRoomsAvailabilityRequest, omhRoomsAvailabilityResponse, omhPreCheckRequest, omhPreCheckResponse);
        return singleSearchResponseConverter.toSearchResponse(
            hotelId,
            orderedRoomAvailability,
            mrtCommissionInfo,
            reservationSearchRequest.getRatePlanCount(),
            zeroMargin,
            String.valueOf(order.getOrderId())
        );
    }

    private OmhRoomAvailability getOrderedOmhRoomAvailability(OmhRoomsAvailabilityResponse omhRoomsAvailabilityResponse,
                                                              RateSearchId rateSearchId) {
        for (OmhRoomAvailability omhRoomAvailability : omhRoomsAvailabilityResponse.getRooms()) {
            if (omhRoomAvailability.getRoomTypeCode().equals(rateSearchId.getRoomTypeCode()) &&
                omhRoomAvailability.getRatePlanCode().equals(rateSearchId.getRatePlanCode())) {
                return omhRoomAvailability.toBuilder().build();
            }
        }
        return null;
    }

    private Order saveOrder(ReservationSearchRequest searchRequest, BigDecimal mrtCommissionRate, OmhRoomAvailability omhRoomAvailability, ZeroMargin zeroMargin) {
        Order order = orderConverter.toOrder(searchRequest, omhRoomAvailability, mrtCommissionRate, zeroMargin);
        return orderProvider.upsert(order);
    }

    private void saveApiLog(Long orderId,
                            OmhRoomsAvailabilityRequest omhRoomsAvailabilityRequest,
                            OmhRoomsAvailabilityResponse omhRoomsAvailabilityResponse,
                            OmhPreCheckRequest omhPreCheckRequest,
                            OmhPreCheckResponse omhPreCheckResponse) {
        reservationApiLogService.saveRoomsAvailabilityLog(orderId, ApiLogType.REQUEST, ObjectMapperUtils.writeAsString(omhRoomsAvailabilityRequest));
        reservationApiLogService.saveRoomsAvailabilityLog(orderId, ApiLogType.RESPONSE, ObjectMapperUtils.writeAsString(omhRoomsAvailabilityResponse));
        reservationApiLogService.savePreCheckLog(String.valueOf(orderId), ApiLogType.REQUEST, ObjectMapperUtils.writeAsString(omhPreCheckRequest));
        reservationApiLogService.savePreCheckLog(String.valueOf(orderId), ApiLogType.RESPONSE, ObjectMapperUtils.writeAsString(omhPreCheckResponse));
    }
}
