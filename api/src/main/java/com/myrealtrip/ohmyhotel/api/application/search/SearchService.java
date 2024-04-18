package com.myrealtrip.ohmyhotel.api.application.search;

import com.myrealtrip.ohmyhotel.api.application.search.converter.MultipleSearchResponseConverter;
import com.myrealtrip.ohmyhotel.api.application.search.converter.SearchRequestConverter;
import com.myrealtrip.ohmyhotel.api.application.search.converter.SingleSearchResponseConverter;
import com.myrealtrip.ohmyhotel.core.service.CommissionRateService;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.avilability.OmhHotelsAvailabilityAgent;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.avilability.OmhRoomsAvailabilityAgent;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.avilability.protocol.OmhHotelsAvailabilityResponse;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.avilability.protocol.OmhRoomsAvailabilityResponse;
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

@Service
@RequiredArgsConstructor
public class SearchService {

    private final CommissionRateService commissionRateService;

    private final OmhHotelsAvailabilityAgent omhHotelsAvailabilityAgent;
    private final OmhRoomsAvailabilityAgent omhRoomsAvailabilityAgent;

    private final MultipleSearchResponseConverter multipleSearchResponseConverter;
    private final SingleSearchResponseConverter singleSearchResponseConverter;
    private final SearchRequestConverter searchRequestConverter;

    /**
     * 숙소의 실시간 재고/가격을 검색합니다.
     */
    @Transactional
    public SearchResponse search(SearchRequest searchRequest) {
        if (CollectionUtils.isEmpty(searchRequest.getPropertyIds())) {
            return SearchResponse.builder()
                .providerCode(ProviderCode.OH_MY_HOTEL)
                .searchId(null)
                .properties(Collections.emptyList())
                .build();
        }

        BigDecimal mrtCommissionRate = commissionRateService.getMrtCommissionRate();
        if (searchRequest.getPropertyIds().size() == 1) {
            OmhRoomsAvailabilityResponse omhRoomsAvailabilityResponse = singleOmhSearch(searchRequest);
            return singleSearchResponseConverter.toSearchResponse(
                Long.valueOf(searchRequest.getPropertyIds().get(0)),
                omhRoomsAvailabilityResponse,
                mrtCommissionRate,
                searchRequest.getRatePlanCount()
            );
        }
        OmhHotelsAvailabilityResponse omhHotelsAvailabilityResponse = multipleOmhSearch(searchRequest);
        return multipleSearchResponseConverter.toSearchResponse(
            omhHotelsAvailabilityResponse,
            mrtCommissionRate,
            searchRequest.getRatePlanCount()
        );
    }

    private OmhHotelsAvailabilityResponse multipleOmhSearch(SearchRequest searchRequest) {
        OmhHotelsAvailabilityRequest omhHotelsAvailabilityRequest = searchRequestConverter.toOmhHotelsAvailabilityRequest(searchRequest);
        return omhHotelsAvailabilityAgent.getHotelsAvailability(omhHotelsAvailabilityRequest);
    }

    private OmhRoomsAvailabilityResponse singleOmhSearch(SearchRequest searchRequest) {
        OmhRoomsAvailabilityRequest omhRoomsAvailabilityRequest = searchRequestConverter.toOmhRoomsAvailabilityRequest(searchRequest);
        return omhRoomsAvailabilityAgent.getRoomsAvailability(omhRoomsAvailabilityRequest);
    }
}
