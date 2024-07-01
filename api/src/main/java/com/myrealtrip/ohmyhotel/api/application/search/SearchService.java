package com.myrealtrip.ohmyhotel.api.application.search;

import com.google.common.collect.Lists;
import com.myrealtrip.ohmyhotel.api.application.search.converter.MultipleSearchResponseConverter;
import com.myrealtrip.ohmyhotel.api.application.common.converter.SearchRequestConverter;
import com.myrealtrip.ohmyhotel.api.application.common.converter.SingleSearchResponseConverter;
import com.myrealtrip.ohmyhotel.api.protocol.search.OmhHotelsAvailabilityCacheRequest;
import com.myrealtrip.ohmyhotel.core.domain.zeromargin.dto.ZeroMargin;
import com.myrealtrip.ohmyhotel.core.service.CommissionRateService;
import com.myrealtrip.ohmyhotel.core.service.ZeroMarginSearchService;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.avilability.OmhHotelsAvailabilityAgent;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.avilability.OmhRoomsAvailabilityAgent;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.avilability.protocol.response.OmhHotelsAvailabilityResponse;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.avilability.protocol.response.OmhHotelsAvailabilityResponse.OmhHotelAvailability;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.avilability.protocol.response.OmhRoomsAvailabilityResponse;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.avilability.protocol.request.OmhHotelsAvailabilityRequest;
import com.myrealtrip.ohmyhotel.outbound.agent.ota.avilability.protocol.request.OmhRoomsAvailabilityRequest;
import com.myrealtrip.unionstay.common.constant.ProviderCode;
import com.myrealtrip.unionstay.dto.hotelota.search.request.SearchRequest;
import com.myrealtrip.unionstay.dto.hotelota.search.response.SearchResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@Service
@RequiredArgsConstructor
@Slf4j
public class SearchService {

    private static final int PARTITION_SIZE = 30;

    private final ExecutorService fixedThreadPool = Executors.newFixedThreadPool(600);

    private final CommissionRateService commissionRateService;
    private final ZeroMarginSearchService zeroMarginSearchService;

    private final OmhHotelsAvailabilityCacheService omhHotelsAvailabilityCacheService;
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

    /**
     * 한번에 최대 350 개의 호텔 조회 요청이 들어올 수 있다.
     * 오마이호텔 API 성능 이슈로 20 개씩 쪼개어 요청을 보낸다.
     */
    private SearchResponse multipleOmhSearch(SearchRequest searchRequest, BigDecimal mrtCommissionRate) {
        List<Long> hotelIds = searchRequest.getPropertyIds().stream()
            .map(Long::valueOf)
            .collect(Collectors.toList());

        Map<Long, ZeroMargin> hotelIdToZeroMargin = zeroMarginSearchService.getZeroMargins(hotelIds, true);

        int parallelRailsCount = (int) Math.ceil((double) searchRequest.getPropertyIds().size() / PARTITION_SIZE);
        List<OmhHotelAvailability> omhHotelAvailabilities = Flux.fromIterable(Lists.partition(hotelIds, PARTITION_SIZE))
            .parallel(parallelRailsCount)
            .runOn(Schedulers.fromExecutorService(fixedThreadPool))
            .map(hotelIdsPartition -> {
                try {
                    OmhHotelsAvailabilityRequest omhHotelsAvailabilityRequest = searchRequestConverter.toOmhHotelsAvailabilityRequest(searchRequest, hotelIdsPartition);
                    OmhHotelsAvailabilityResponse omhHotelsAvailabilityResponse = omhHotelsAvailabilityCacheService.getHotelsAvailability(new OmhHotelsAvailabilityCacheRequest(omhHotelsAvailabilityRequest));
                    return omhHotelsAvailabilityResponse.getHotels();
                } catch (Exception e) {
                    log.error("hotels availability api error", e);
                    return new ArrayList<OmhHotelAvailability>();
                }
            })
            .flatMap(Flux::fromIterable)
            .sequential()
            .collectList()
            .block();

        return multipleSearchResponseConverter.toSearchResponse(
            omhHotelAvailabilities,
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
