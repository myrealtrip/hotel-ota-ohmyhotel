package com.myrealtrip.ohmyhotel.api.application.search;

import com.myrealtrip.ohmyhotel.api.application.search.converter.MultipleSearchResponseConverter;
import com.myrealtrip.ohmyhotel.api.application.common.converter.SearchRequestConverter;
import com.myrealtrip.ohmyhotel.api.application.common.converter.SingleSearchResponseConverter;
import com.myrealtrip.ohmyhotel.core.domain.zeromargin.dto.ZeroMargin;
import com.myrealtrip.ohmyhotel.core.service.CommissionRateService;
import com.myrealtrip.ohmyhotel.core.service.ZeroMarginSearchService;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.avilability.OmhHotelsAvailabilityAgent;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.avilability.OmhRoomsAvailabilityAgent;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.avilability.protocol.response.OmhHotelsAvailabilityResponse;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.avilability.protocol.response.OmhRoomsAvailabilityResponse;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.avilability.protocol.request.OmhHotelsAvailabilityRequest;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.avilability.protocol.request.OmhRoomsAvailabilityRequest;
import com.myrealtrip.unionstay.common.constant.ProviderCode;
import com.myrealtrip.unionstay.dto.hotelota.search.request.SearchRequest;
import com.myrealtrip.unionstay.dto.hotelota.search.response.SearchResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final CommissionRateService commissionRateService;
    private final ZeroMarginSearchService zeroMarginSearchService;

    private final OmhHotelsAvailabilityAgent omhHotelsAvailabilityAgent;
    private final OmhRoomsAvailabilityAgent omhRoomsAvailabilityAgent;

    private final MultipleSearchResponseConverter multipleSearchResponseConverter;
    private final SingleSearchResponseConverter singleSearchResponseConverter;
    private final SearchRequestConverter searchRequestConverter;

    /**
     * 숙소의 실시간 재고/가격을 검색합니다. (검색 리스트, 상품 상세 에서 호출)
     */
    @Transactional(readOnly = true)
    public SearchResponse search(SearchRequest searchRequest) {
        if (CollectionUtils.isEmpty(searchRequest.getPropertyIds())) {
            return SearchResponse.builder()
                .providerCode(ProviderCode.OH_MY_HOTEL)
                .searchId(ProviderCode.OH_MY_HOTEL.name())
                .properties(Collections.emptyList())
                .build();
        }

        BigDecimal mrtCommissionRate = commissionRateService.getMrtCommissionRate();
        if (searchRequest.getPropertyIds().size() == 1) {
            return singleOmhSearch(searchRequest, mrtCommissionRate);
        }
        return multipleOmhSearch(searchRequest, mrtCommissionRate);
    }

    private SearchResponse multipleOmhSearch(SearchRequest searchRequest, BigDecimal mrtCommissionRate) {
        List<Long> hotelIds = searchRequest.getPropertyIds().stream()
            .map(Long::valueOf)
            .collect(Collectors.toList());
        Map<Long, ZeroMargin> hotelIdToZeroMargin = zeroMarginSearchService.getZeroMargins(hotelIds, true);
        OmhHotelsAvailabilityRequest omhHotelsAvailabilityRequest = searchRequestConverter.toOmhHotelsAvailabilityRequest(searchRequest);
        OmhHotelsAvailabilityResponse omhHotelsAvailabilityResponse =  omhHotelsAvailabilityAgent.getHotelsAvailability(omhHotelsAvailabilityRequest);
        return multipleSearchResponseConverter.toSearchResponse(
            omhHotelsAvailabilityResponse,
            mrtCommissionRate,
            searchRequest.getRatePlanCount(),
            hotelIdToZeroMargin
        );
    }

    private SearchResponse singleOmhSearch(SearchRequest searchRequest, BigDecimal mrtCommissionRate) {
        ZeroMargin zeroMargin = zeroMarginSearchService.getZeroMargin(Long.valueOf(searchRequest.getPropertyIds().get(0)), true);
        OmhRoomsAvailabilityRequest omhRoomsAvailabilityRequest = searchRequestConverter.toOmhRoomsAvailabilityRequest(searchRequest);
        OmhRoomsAvailabilityResponse omhRoomsAvailabilityResponse = omhRoomsAvailabilityAgent.getRoomsAvailability(omhRoomsAvailabilityRequest);
        return singleSearchResponseConverter.toSearchResponse(
            Long.valueOf(searchRequest.getPropertyIds().get(0)),
            omhRoomsAvailabilityResponse,
            mrtCommissionRate,
            searchRequest.getRatePlanCount(),
            zeroMargin
        );
    }
}
