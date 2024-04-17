package com.myrealtrip.ohmyhotel.api.application.search;

import com.myrealtrip.ohmyhotel.api.application.search.converter.MultipleSearchResponseConverter;
import com.myrealtrip.ohmyhotel.api.application.search.converter.SearchRequestConverter;
import com.myrealtrip.ohmyhotel.api.application.search.converter.SingleSearchResponseConverter;
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

    private final BigDecimal mrtCommissionRate = BigDecimal.valueOf(20); // TODO 파트너 정보 연동 필요

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
        if (searchRequest.getPropertyIds().size() == 1) {
            OmhRoomsAvailabilityRequest omhRoomsAvailabilityRequest = searchRequestConverter.toOmhRoomsAvailabilityRequest(searchRequest);
            OmhRoomsAvailabilityResponse omhRoomsAvailabilityResponse = omhRoomsAvailabilityAgent.getRoomsAvailability(omhRoomsAvailabilityRequest);
            return singleSearchResponseConverter.toSearchResponse(
                Long.valueOf(searchRequest.getPropertyIds().get(0)),
                omhRoomsAvailabilityResponse,
                mrtCommissionRate,
                searchRequest.getRatePlanCount()
            );
        }
        OmhHotelsAvailabilityRequest omhHotelsAvailabilityRequest = searchRequestConverter.toOmhHotelsAvailabilityRequest(searchRequest);
        OmhHotelsAvailabilityResponse omhHotelsAvailabilityResponse = omhHotelsAvailabilityAgent.getHotelsAvailability(omhHotelsAvailabilityRequest);
        return multipleSearchResponseConverter.toSearchResponse(
            omhHotelsAvailabilityResponse,
            mrtCommissionRate,
            searchRequest.getRatePlanCount()
        );
    }
}
